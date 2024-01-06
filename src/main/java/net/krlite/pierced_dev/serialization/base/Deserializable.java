package net.krlite.pierced_dev.serialization.base;

import java.util.Optional;

@FunctionalInterface
public interface Deserializable<T> {
    /**
     * Deserializes the {@link String} value to an {@link Optional} with a {@link T} object.
     * @param value the read {@link String} value.
     * @return  The deserialized {@link T} object wrapped in an {@link Optional}.
     */
    Optional<T> deserialize(String value);
}
