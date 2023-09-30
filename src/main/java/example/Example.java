package example;

import net.krlite.pierced_dev.Pierced;
import net.krlite.pierced_dev.annotation.Comment;
import net.krlite.pierced_dev.annotation.InlineComment;
import net.krlite.pierced_dev.annotation.Table;
import net.krlite.pierced_dev.ast.io.Reader;
import net.krlite.pierced_dev.ast.regex.NewLine;
import net.krlite.pierced_dev.ast.regex.Whitespace;
import net.krlite.pierced_dev.ast.regex.primitive.BasicString;
import net.krlite.pierced_dev.ast.regex.primitive.MultilineBasicString;
import net.krlite.pierced_dev.ast.util.Util;
import net.krlite.pierced_dev.serialization.PrimitiveSerializers;

import java.awt.*;
import java.io.File;

public class Example {
    public static void main(String[] args) {
        /*
        System.out.println("=== UTIL ===");
        System.out.println("raw: " + "\\u0041,\\U0001F600");
        System.out.println("unescaped: " + Util.unescape("\\u0041,\\U0001F600"));

        System.out.println();

        System.out.println("raw: " + "\\,\",\b,\f,\n,\r,\t,\\u0000,\\U00000000");
        System.out.println("escaped: " + Util.escape("\\,\",\b,\f,\n,\r,\t,\\u0000,\\U00000000", true));

         */

        System.out.println("=== READ ===");
        Reader reader = new Reader(new File("src/main/java/example/config/example.toml"));

        reader.get(
                "a_string",
                PrimitiveSerializers.getPrimitiveSerializer(String.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "a_multiline_string",
                PrimitiveSerializers.getPrimitiveSerializer(String.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "a_literal_string",
                PrimitiveSerializers.getPrimitiveSerializer(String.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "cat.a_boolean",
                PrimitiveSerializers.getPrimitiveSerializer(Boolean.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "cat.a_nan",
                PrimitiveSerializers.getPrimitiveSerializer(Double.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "an_infinity",
                PrimitiveSerializers.getPrimitiveSerializer(Double.class).get()
        ).ifPresent(System.out::println);

        /*
        reader.get(
                "a.\"23.k\".1.3",
                PrimitiveSerializers.getPrimitiveSerializer(boolean.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "b.d",
                PrimitiveSerializers.getPrimitiveSerializer(double.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "c",
                PrimitiveSerializers.getPrimitiveSerializer(Color.class).get()
        ).ifPresent(System.out::println);

         */

        /*
        Config config = new Config();

        System.out.println("=== LOAD ===");
        config.load();
        System.out.println(config.c);

        System.out.println("=== SAVE ===");
        config.save();

         */
    }

    @Comment("A class")
    public static class Config extends Pierced {
        public Config() {
            super(Config.class, new File("src/main/java/example/config/test.toml"));
        }

        @Comment("ABC")
        @InlineComment("INLINE")
        @Table("a . \"b\"")
        public Color c = new Color(0x172D8F);

        @Comment("DEF")
        public double aaa = 2.3;
    }
}
