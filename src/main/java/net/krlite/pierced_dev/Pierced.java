package net.krlite.pierced_dev;

import net.krlite.pierced_dev.annotation.Silent;
import net.krlite.pierced_dev.annotation.Table;
import net.krlite.pierced_dev.ast.io.Reader;
import net.krlite.pierced_dev.ast.util.Util;
import net.krlite.pierced_dev.serialization.PrimitiveSerializers;
import net.krlite.pierced_dev.serialization.RecursiveSerializers;
import net.krlite.pierced_dev.serialization.base.Serializer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public abstract class Pierced {
    private @Silent final Class<? extends Pierced> clazz;
    private @Silent final Reader reader;
    private @Silent final HashMap<Field, Serializer<?>> serializers = new HashMap<>();

    protected Pierced(Class<? extends Pierced> clazz, File file) {
        this.clazz = clazz;
        this.reader = new Reader(file);
    }

    protected File file() {
        return reader.file();
    }

    public HashMap<Long, Exception> exceptions() {
        return reader.exceptions();
    }

    private <S> Optional<Serializer<S>> getSerializer(Field field, Class<S> sClass) {
        return Optional.ofNullable(RecursiveSerializers.getRecursiveSerializer(sClass)
                .orElse(PrimitiveSerializers.getPrimitiveSerializer(sClass)
                        .orElse((Serializer<S>) serializers.get(field))));
    }

    protected <S> void registerSerializerFor(String fieldName, Serializer<S> serializer) {
        try {
            Field field = clazz.getField(fieldName);
            serializers.put(field, serializer);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field '" + fieldName + "' does not exist:", e);
        }
    }

    protected void removeSerializerFor(String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            serializers.remove(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field '" + fieldName + "' does not exist:", e);
        }
    }

    public void load() {
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields)
                .filter(this::isValid)
                .forEach(field -> getSerializer(field, field.getType())
                        .flatMap(serializer -> reader.get(getKey(field), serializer))
                        .ifPresent(value -> {
                            try {
                                field.set(this, value);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException("Cannot set field '" + field.getName() + "':", e);
                            }
                        }));
    }

    private boolean isValid(Field field) {
        return !field.isAnnotationPresent(Silent.class);
    }

    private String getKey(Field field) {
        String name = field.getName();
        if (field.isAnnotationPresent(Table.class))
            return Util.normalizeKey(field.getAnnotation(Table.class).value() + "." + name);
        else return name;
    }
}
