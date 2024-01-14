package net.krlite.pierced.serialization.base;

import java.util.Optional;

@FunctionalInterface
public interface Deserializable<S> {
    /**
     * Deserializes the {@link String} value to an {@link Optional} with a {@link S} object.
     * @param sClass the class of the instance.
     * @param value the read {@link String} value.
     * @return  The deserialized {@link S} object wrapped in an {@link Optional}.
     */
    Optional<S> deserialize(Class<S> sClass, String value);
}
