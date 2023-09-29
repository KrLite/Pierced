package net.krlite.pierced_dev.serialization.base;

import java.util.Optional;

@FunctionalInterface
public interface Deserializable<T> {
    Optional<T> deserialize(String value);
}
