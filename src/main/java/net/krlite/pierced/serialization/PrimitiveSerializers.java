package net.krlite.pierced.serialization;

import net.krlite.pierced.ast.regex.NewLine;
import net.krlite.pierced.ast.regex.primitive.*;
import net.krlite.pierced.ast.regex.primitive.Integer;
import net.krlite.pierced.serialization.base.Serializer;

import java.awt.*;
import java.lang.Boolean;
import java.lang.Float;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;

public class PrimitiveSerializers {
    public static final Serializer<String> STRING = Serializer.build(PrimitiveSerializers::parseString);
    public static final Serializer<Character> CHARACTER = Serializer.build((c, s) -> Optional.of(s.toCharArray()[0]));
    public static final Serializer<Boolean> BOOLEAN = Serializer.build(PrimitiveSerializers::parseBoolean);
    public static final Serializer<Byte> BYTE = Serializer.build((c, s) -> Optional.of(s.getBytes()[0]));
    public static final Serializer<Short> SHORT = Serializer.build(PrimitiveSerializers::parseShort);
    public static final Serializer<java.lang.Integer> INTEGER = Serializer.build(PrimitiveSerializers::parseInt);
    public static final Serializer<Long> LONG = Serializer.build(PrimitiveSerializers::parseLong);
    public static final Serializer<Float> FLOAT = Serializer.build(PrimitiveSerializers::parseFloat);
    public static final Serializer<Double> DOUBLE = Serializer.build(PrimitiveSerializers::parseDouble);
    public static final Serializer<Color> COLOR = Serializer.build(PrimitiveSerializers::parseColor, PrimitiveSerializers::formatColor);
    public static final Serializer<? extends Enum<?>> ENUM = Serializer.build(PrimitiveSerializers::parseEnum, PrimitiveSerializers::formatEnum);

    public static Optional<String> parseString(Class<String> stringClass, String value) {
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
        if (BasicString.BASIC_STRING.matcher(value).matches()) {
            value = value.replaceAll("^" + BasicString.QUOTATION_MARK, "");
            value = value.replaceAll(BasicString.QUOTATION_MARK + "$", "");

            return Optional.of(value);
        }

        // Literal string
        if (LiteralString.LITERAL_STRING.matcher(value).matches()) {
            value = value.replaceAll("^" + LiteralString.APOSTROPHE, "");
            value = value.replaceAll(LiteralString.APOSTROPHE + "$", "");

            return Optional.of(value);
        }

        return Optional.empty();
    }

    public static Optional<Boolean> parseBoolean(Class<Boolean> booleanClass, String value) {
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

    public static Optional<Short> parseShort(Class<Short> shortClass, String value) {
        return parseRadix(value).map(Long::shortValue);
    }

    public static Optional<java.lang.Integer> parseInt(Class<java.lang.Integer> integerClass, String value) {
        return parseRadix(value).map(Long::intValue);
    }

    public static Optional<Long> parseLong(Class<Long> longClass, String value) {
        return parseRadix(value);
    }

    public static Optional<Float> parseFloat(Class<Float> floatClass, String value) {
        return parseFloatingPoint(value).map(Double::floatValue);
    }

    public static Optional<Double> parseDouble(Class<Double> doubleClass, String value) {
        return parseFloatingPoint(value);
    }

    public static Optional<Color> parseColor(Class<Color> colorClass, String value) {
        return PrimitiveSerializers.parseLong(Long.class, value)
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

    public static <E extends Enum<?>> Optional<E> parseEnum(Class<E> enumClass, String value) {
        if (!enumClass.isEnum()) return Optional.empty();

        Optional<String> enumString = parseString(String.class, value);
        return enumString.flatMap(s -> Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> s.equals(e.toString()))
                .findAny()
        );
    }

    public static <E extends Enum<?>> String formatEnum(E value) {
        return value.toString();
    }

    public static <S> Optional<Serializer<S>> getSerializer(Class<S> sClass) {
        if (sClass == String.class)
            return Optional.of((Serializer<S>) STRING);

        if (sClass == Character.class || sClass == char.class)
            return Optional.of((Serializer<S>) CHARACTER);

        if (sClass == Boolean.class || sClass == boolean.class)
            return Optional.of((Serializer<S>) BOOLEAN);

        if (sClass == Byte.class || sClass == byte.class)
            return Optional.of((Serializer<S>) BYTE);

        if (sClass == Short.class || sClass == short.class)
            return Optional.of((Serializer<S>) SHORT);

        if (sClass == Integer.class || sClass == int.class)
            return Optional.of((Serializer<S>) INTEGER);

        if (sClass == Long.class || sClass == long.class)
            return Optional.of((Serializer<S>) LONG);

        if (sClass == Float.class || sClass == float.class)
            return Optional.of((Serializer<S>) FLOAT);

        if (sClass == Double.class || sClass == double.class)
            return Optional.of((Serializer<S>) DOUBLE);

        if (sClass == Color.class)
            return Optional.of((Serializer<S>) COLOR);

        if (sClass.isEnum())
            return Optional.of((Serializer<S>) ENUM);

        return Optional.empty();
    }
}
