package net.krlite.pierced_dev.serialization;

import net.krlite.pierced_dev.ast.regex.recursive.Array;
import net.krlite.pierced_dev.serialization.base.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class RecursiveSerializers {
    public static final Serializer<List<?>> LIST = Serializer.build(s -> {
        Matcher matcher = Array.ARRAY_LAYER.matcher(s);
        System.out.println(s);
        while (matcher.matches()) {
            String layer = matcher.group("value");
            System.out.println(layer);
        }
        return Optional.empty();
    });

    public static <T> Optional<Serializer<T>> getSerializer(Class<T> tClass) {
        if (List.class.isAssignableFrom(tClass))
            return Optional.of((Serializer<T>) LIST);

        return Optional.empty();
    }
}
