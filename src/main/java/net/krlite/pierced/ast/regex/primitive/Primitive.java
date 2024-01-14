package net.krlite.pierced.ast.regex.primitive;

import net.krlite.pierced.ast.regex.ABNF;

import java.util.regex.Pattern;

public class Primitive extends ABNF {
    public static final Pattern STRING = or(
            MultilineBasicString.ML_BASIC_STRING,
            BasicString.BASIC_STRING,
            MultilineLiteralString.ML_LITERAL_STRING,
            LiteralString.LITERAL_STRING
    );

    public static final Pattern PRIMITIVE = or(
            Boolean.BOOLEAN,
            Integer.INTEGER,
            Float.FLOAT,
            STRING
    );
}
