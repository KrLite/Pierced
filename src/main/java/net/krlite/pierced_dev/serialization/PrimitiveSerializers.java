package net.krlite.pierced_dev.serialization;

import net.krlite.pierced_dev.ast.regex.NewLine;
import net.krlite.pierced_dev.ast.regex.primitive.Integer;
import net.krlite.pierced_dev.ast.regex.primitive.MultilineBasicString;
import net.krlite.pierced_dev.ast.regex.primitive.MultilineLiteralString;
import net.krlite.pierced_dev.serialization.base.Serializer;

import java.awt.*;
import java.util.Optional;
import java.util.regex.Matcher;

public class PrimitiveSerializers {
    public static final Serializer<String> STRING = Serializer.build(PrimitiveSerializers::parseString);
    public static final Serializer<Character> CHARACTER = Serializer.build(s -> Optional.of(s.toCharArray()[0]));
    public static final Serializer<Boolean> BOOLEAN = Serializer.build(PrimitiveSerializers::parseBoolean);
    public static final Serializer<Byte> BYTE = Serializer.build(s -> Optional.of(s.getBytes()[0]));
    public static final Serializer<Short> SHORT = Serializer.build(PrimitiveSerializers::parseShort);
    public static final Serializer<java.lang.Integer> INTEGER = Serializer.build(PrimitiveSerializers::parseInt);
    public static final Serializer<Long> LONG = Serializer.build(PrimitiveSerializers::parseLong);
    public static final Serializer<Float> FLOAT = Serializer.build(PrimitiveSerializers::parseFloat);
    public static final Serializer<Double> DOUBLE = Serializer.build(PrimitiveSerializers::parseDouble);
    public static final Serializer<Color> COLOR = Serializer.build(PrimitiveSerializers::parseColor, PrimitiveSerializers::formatColor);

    public static Optional<String> parseString(String value) {
        // Multiline basic string
        if (MultilineBasicString.ML_BASIC_STRING.matcher(value).matches()) {
            // Remove delims
            value = value.replaceAll("^" + MultilineBasicString.ML_BASIC_STRING_DELIM.pattern() + "(" + NewLine.NEWLINE.pattern() + "|)", "");
            value = value.replaceAll(MultilineBasicString.ML_BASIC_STRING_DELIM.pattern() + "$", "");

            // Remove escaped new lines
            value = value.replaceAll(MultilineBasicString.MLB_ESCAPED_NL.pattern(), "");

            return Optional.of(value);
        }

        // Multiline literal string
        if (MultilineLiteralString.ML_LITERAL_STRING.matcher(value).matches()) {
            // Remove delims
            value = value.replaceAll("^" + MultilineLiteralString.ML_LITERAL_STRING_DELIM.pattern(), "");
            value = value.replaceAll(MultilineLiteralString.ML_LITERAL_STRING_DELIM.pattern() + "$", "");

            return Optional.of(value);
        }

        // Basic string
        return Optional.of(value);
    }

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

        if (value.endsWith("nan"))
            return Optional.of(Double.NaN);

        if (value.endsWith("inf")) {
            return Optional.of(value.startsWith("-") ? Double.MIN_VALUE : Double.MAX_VALUE);
        }

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

    public static Optional<Color> parseColor(String value) {
        return PrimitiveSerializers.parseLong(value)
                .map(longValue -> {
                    int a = (int) ((longValue & 0xFF000000L) >> 24);
                    int r = (int) ((longValue & 0xFF0000) >> 16);
                    int g = (int) ((longValue & 0xFF00) >> 8);
                    int b = (int) (longValue & 0xFF);

                    return new Color(r, g, b, a);
                });
    }

    public static String formatColor(Color color) {
        return String.format("0x%08X", color.getRGB());
    }

    public static <T> Optional<Serializer<T>> getSerializer(Class<T> tClass) {
        if (tClass == String.class)
            return Optional.of((Serializer<T>) STRING);

        if (tClass == Character.class || tClass == char.class)
            return Optional.of((Serializer<T>) CHARACTER);

        if (tClass == Boolean.class || tClass == boolean.class)
            return Optional.of((Serializer<T>) BOOLEAN);

        if (tClass == Byte.class || tClass == byte.class)
            return Optional.of((Serializer<T>) BYTE);

        if (tClass == Short.class || tClass == short.class)
            return Optional.of((Serializer<T>) SHORT);

        if (tClass == Integer.class || tClass == int.class)
            return Optional.of((Serializer<T>) INTEGER);

        if (tClass == Long.class || tClass == long.class)
            return Optional.of((Serializer<T>) LONG);

        if (tClass == Float.class || tClass == float.class)
            return Optional.of((Serializer<T>) FLOAT);

        if (tClass == Double.class || tClass == double.class)
            return Optional.of((Serializer<T>) DOUBLE);

        if (tClass == Color.class)
            return Optional.of((Serializer<T>) COLOR);

        return Optional.empty();
    }
}
