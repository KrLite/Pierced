package net.krlite.pierced_dev.serialization;

import net.krlite.pierced_dev.ast.regex.primitive.Integer;
import net.krlite.pierced_dev.serialization.base.Serializer;

import java.util.Optional;
import java.util.regex.Matcher;

public class PrimitiveSerializers {
    public static final Serializer<String> STRING = Serializer.build(Optional::of);
    public static final Serializer<Character> CHARACTER = Serializer.build(s -> Optional.of(s.toCharArray()[0]));
    public static final Serializer<Boolean> BOOLEAN = Serializer.build(PrimitiveSerializers::parseBoolean);
    public static final Serializer<Byte> BYTE = Serializer.build(s -> Optional.of(s.getBytes()[0]));
    public static final Serializer<Short> SHORT = Serializer.build(PrimitiveSerializers::parseShort);
    public static final Serializer<java.lang.Integer> INTEGER = Serializer.build(PrimitiveSerializers::parseInt);
    public static final Serializer<Long> LONG = Serializer.build(PrimitiveSerializers::parseLong);
    public static final Serializer<Float> FLOAT = Serializer.build(PrimitiveSerializers::parseFloat);
    public static final Serializer<Double> DOUBLE = Serializer.build(PrimitiveSerializers::parseDouble);

    public static Optional<Boolean> parseBoolean(String value) {
        return Optional.of(Boolean.parseBoolean(value));
    }

    public static Optional<Long> parseRadix(String value) {
        value = value.replaceAll("_", "");

        Matcher hexMatcher = Integer.HEX_INT.matcher(value);
        boolean hexMatched = hexMatcher.matches();

        if (hexMatched)
            return Optional.of(Long.parseLong(value.replaceAll(Integer.HEX_PREFIX.pattern(), ""), 16));

        Matcher octMatcher = Integer.OCT_INT.matcher(value);
        boolean octMatched = octMatcher.matches();

        if (octMatched)
            return Optional.of(Long.parseLong(value.replaceAll(Integer.OCT_PREFIX.pattern(), ""), 8));

        Matcher binMatcher = Integer.BIN_INT.matcher(value);
        boolean binMatched = binMatcher.matches();

        if (binMatched)
            return Optional.of(Long.parseLong(value.replaceAll(Integer.BIN_PREFIX.pattern(), ""), 2));

        Matcher decMatcher = Integer.DEC_INT.matcher(value);
        boolean decMatched = decMatcher.matches();

        if (decMatched)
            return Optional.of(Long.parseLong(value));

        return Optional.empty();
    }

    public static Optional<Double> parseFloatingPoint(String value) {
        value = value.replaceAll("_", "");
        return Optional.of(Double.parseDouble(value));
    }

    public static Optional<Short> parseShort(String value) {
        return parseRadix(value).map(Long::shortValue);
    }

    public static Optional<java.lang.Integer> parseInt(String value) {
        return parseRadix(value).map(Long::intValue);
    }

    public static Optional<Long> parseLong(String value) {
        return parseRadix(value);
    }

    public static Optional<Float> parseFloat(String value) {
        return parseFloatingPoint(value).map(Double::floatValue);
    }

    public static Optional<Double> parseDouble(String value) {
        return parseFloatingPoint(value);
    }
}
