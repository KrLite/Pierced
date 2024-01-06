package net.krlite.pierced_dev.serialization.base;

@FunctionalInterface
public interface Serializable<T> {
    /**
     * Serializes the {@link T} object to a {@link String} value.
     * @param object the {@link T} object.
     * @return  The serialized {@link String} value.
     */
    String serialize(T object);
}
