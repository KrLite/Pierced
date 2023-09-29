package net.krlite.pierced_dev.serialization;

import net.krlite.pierced_dev.serialization.base.Serializer;

import java.util.Optional;

public class RecursiveSerializers {
    public static <T> Optional<Serializer<T>> getRecursiveSerializer(Class<T> tClass) {
        return Optional.empty();
    }
}
