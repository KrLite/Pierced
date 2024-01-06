package net.krlite.pierced_dev.serialization.base;

import java.util.Optional;

public interface Serializer<T> extends Deserializable<T>, Serializable<T> {
    /**
     * Builds a {@link Serializer} from a deserialization function and a serialization function.
     * @param deserializable    the deserialization function.
     * @param serializable      the serialization function.
     * @return  The built {@link Serializer}.
     * @param <S>   The target class of the {@link Serializer}.
     */
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

    /**
     * Builds a {@link Serializer} from a deserialization function and a default {@link String#valueOf(Object)}
     * serialization function.
     * @param deserializable    the deserialization function.
     * @return  The built {@link Serializer}.
     * @param <S>   The target class of the {@link Serializer}.
     */
    static <S> Serializer<S> build(Deserializable<S> deserializable) {
        return build(deserializable, String::valueOf);
    }
}
