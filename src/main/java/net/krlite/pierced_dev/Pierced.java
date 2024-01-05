package net.krlite.pierced_dev;

import net.krlite.pierced_dev.annotation.*;
import net.krlite.pierced_dev.ast.io.Reader;
import net.krlite.pierced_dev.ast.io.Writer;
import net.krlite.pierced_dev.ast.util.Util;
import net.krlite.pierced_dev.serialization.PrimitiveSerializers;
import net.krlite.pierced_dev.serialization.RecursiveSerializers;
import net.krlite.pierced_dev.serialization.base.Serializer;

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

    private <S> Optional<Serializer<S>> getSerializer(Field field, Class<S> sClass) {
        if (Serializer.class.isAssignableFrom(clazz)) {
            try {
                return Optional.ofNullable((Serializer<S>) field.get(this));
            } catch (IllegalAccessException e) {
                addException(new RuntimeException(e));
            }
        }

        Optional<Serializer<S>>
                registeredSerializer =  Optional.ofNullable((Serializer<S>) serializers.get(field)),
                recursiveSerializer = RecursiveSerializers.getSerializer(sClass),
                primitiveSerializer = PrimitiveSerializers.getSerializer(sClass);

        if (registeredSerializer.isPresent()) return registeredSerializer;
        if (recursiveSerializer.isPresent()) return recursiveSerializer;
        return primitiveSerializer;
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
        getSerializer(field, field.getType())
                .flatMap(serializer -> reader.get(getKey(field), serializer))
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
        // Comment(s)
        writer.writeComments(field, false);

        // Key value pair
        field.setAccessible(true);
        getSerializer(field, field.getType())
                .ifPresent(serializer -> {
                    try {
                        writer.set(field.getName(), field.get(this), serializer);
                    } catch (IllegalAccessException e) {
                        addException(ExceptionHandler.handleFieldIllegalAccessException(e, field.getName()));
                    }
                });

        // Inline Comment
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
