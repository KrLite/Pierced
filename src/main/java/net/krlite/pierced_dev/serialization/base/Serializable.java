package net.krlite.pierced_dev.serialization.base;

@FunctionalInterface
public interface Serializable<T> {
    String serialize(T object);
}
