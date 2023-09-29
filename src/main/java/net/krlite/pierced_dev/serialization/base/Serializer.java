package net.krlite.pierced_dev.serialization.base;

import java.util.Optional;

public interface Serializer<T> extends Deserializable<T>, Serializable<T> {
    static <S> Serializer<S> build(Deserializable<S> deserializable, Serializable<S> serializable) {
        return new Serializer<S>() {
            @Override
            public Optional<S> deserialize(String value) {
                return deserializable.deserialize(value);
            }

            @Override
            public String serialize(S object) {
                return serializable.serialize(object);
            }
        };
    }

    static <S> Serializer<S> build(Deserializable<S> deserializable) {
        return build(deserializable, String::valueOf);
    }
}
