package net.krlite.pierced.serialization.base;

@FunctionalInterface
public interface Serializable<S> {
    /**
     * Serializes the {@link S} object to a {@link String} value.
     * @param object the {@link S} object.
     * @return  The serialized {@link String} value.
     */
    String serialize(S object);
}
