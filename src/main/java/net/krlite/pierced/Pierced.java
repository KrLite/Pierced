package net.krlite.pierced;

import net.krlite.pierced.annotation.*;
import net.krlite.pierced.ast.io.Reader;
import net.krlite.pierced.ast.io.Writer;
import net.krlite.pierced.ast.util.Util;
import net.krlite.pierced.serialization.PrimitiveSerializers;
import net.krlite.pierced.serialization.RecursiveSerializers;
import net.krlite.pierced.serialization.base.Serializer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Pierced extends WithFile {
    private @Silent final Class<? extends Pierced> clazz;
    private @Silent final Reader reader;
    private @Silent final Writer writer;
    private @Silent final HashMap<Field, Serializer<?>> serializers = new HashMap<>();

    protected Pierced(Class<? extends Pierced> clazz, File file) {
        super(file);
        this.clazz = clazz;
        this.reader = new Reader(file);
        this.writer = new Writer(file);
    }

    @Override
    public File file() {
        return super.file();
    }

    @Override
    public HashMap<Long, Exception> exceptions() {
        return super.exceptions();
    }

    public HashMap<Long, Exception> readerExceptions() {
        return reader.exceptions();
    }

    public HashMap<Long, Exception> writerExceptions() {
        return writer.exceptions();
    }

    private <S> Optional<Serializer.Wrapper<S>> getWrapper(Field field, Class<S> sClass) {
        Serializer<S> serializer = null;

        if (Serializer.class.isAssignableFrom(clazz)) {
            try {
                serializer = (Serializer<S>) field.get(this);
            } catch (IllegalAccessException e) {
                addException(new RuntimeException(e));
            }
        }

        else {
            Optional<Serializer<S>>
                    registeredSerializer =  Optional.ofNullable((Serializer<S>) serializers.get(field)),
                    recursiveSerializer = RecursiveSerializers.getSerializer(sClass),
                    primitiveSerializer = PrimitiveSerializers.getSerializer(sClass);

            if (registeredSerializer.isPresent()) serializer = registeredSerializer.get();
            if (recursiveSerializer.isPresent()) serializer = recursiveSerializer.get();
            if (primitiveSerializer.isPresent()) serializer = primitiveSerializer.get();
        }

        return Optional.ofNullable(serializer)
                .map(s -> new Serializer.Wrapper<S>() {
                    @Override
                    public Serializer<S> serializer() {
                        return s;
                    }

                    @Override
                    public Class<S> sClass() {
                        return sClass;
                    }
                });
    }

    protected <S> void registerSerializerFor(String fieldName, Serializer<S> serializer) {
        try {
            Field field = clazz.getField(fieldName);
            serializers.put(field, serializer);
        } catch (NoSuchFieldException e) {
            addException(ExceptionHandler.handleFieldDoesNotExistException(e, fieldName));
        }
    }

    protected void removeSerializerFor(String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            serializers.remove(field);
        } catch (NoSuchFieldException e) {
            addException(ExceptionHandler.handleFieldDoesNotExistException(e, fieldName));
        }
    }

    public void load() {
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields)
                .filter(this::isValid)
                .forEach(this::load);
    }

    public void load(String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            load(field);
        } catch (NoSuchFieldException e) {
            addException(ExceptionHandler.handleFindFieldException(e, fieldName));
        }
    }

    private void load(Field field) {
        field.setAccessible(true);

        getWrapper(field, field.getType())
                .flatMap(wrapper -> reader.get(getKey(field), wrapper))
                .ifPresent(value -> {
                    try {
                        field.set(this, value);
                    } catch (IllegalAccessException e) {
                        addException(ExceptionHandler.handleSetFieldException(e, field.getName()));
                    }
                });
    }

    public void save() {
        writer.init();

        // Type comment(s)
        writer.writeComments(clazz, true);

        Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isValid)
                .toArray(Field[]::new);

        // Uncategorized fields
        Arrays.stream(fields)
                .filter(field -> !isCategorized(field))
                .forEach(this::save);

        // Categorized fields
        Arrays.stream(fields)
                .filter(this::isCategorized)
                .collect(Collectors.groupingBy(f -> f.getAnnotation(Table.class)))
                .forEach((table, categorizedFields) -> {
                    if (categorizedFields.stream().anyMatch(this::hasValue)) {
                        writer.writeTableComments(clazz, table);
                        writer.writeTable(table);

                        categorizedFields.stream()
                                .filter(this::hasValue)
                                .forEach(this::save);
                    }
                });
    }

    private void save(Field field) {
        field.setAccessible(true);

        // Comment(s)
        writer.writeComments(field, false);

        // Key value pair
        getWrapper(field, field.getType())
                .ifPresent(wrapper -> {
                    try {
                        writer.set(field.getName(), field.get(this), wrapper);
                    } catch (IllegalAccessException e) {
                        addException(ExceptionHandler.handleFieldIllegalAccessException(e, field.getName()));
                    }
                });

        // Inline comment
        writer.writeInlineComment(field);
    }

    private boolean isValid(Field field) {
        return !field.isAnnotationPresent(Silent.class);
    }

    private boolean isCategorized(Field field) {
        return field.isAnnotationPresent(Table.class);
    }

    private boolean hasValue(Field field) {
        try {
            field.setAccessible(true);
            return field.get(this) != null;
        } catch (IllegalAccessException e) {
            addException(ExceptionHandler.handleFieldIllegalAccessException(e, field.getName()));
            return false;
        }
    }

    private String getKey(Field field) {
        String name = field.getName();
        if (field.isAnnotationPresent(Table.class))
            return Util.normalizeKey(field.getAnnotation(Table.class).value() + "." + name);
        else return name;
    }
}
