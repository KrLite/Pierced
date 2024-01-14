package example;

import net.krlite.pierced.Pierced;
import net.krlite.pierced.annotation.Comment;
import net.krlite.pierced.annotation.InlineComment;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.annotation.TableComment;
import net.krlite.pierced.ast.util.Util;

import java.awt.*;
import java.io.File;

public class Example {
    public static void main(String[] args) {
        //util();
        //read();

        Config config = new Config();

        load(config);
        save(config);
    }

    public static void util() {
        System.out.println();
        System.out.println("=== UTIL ===");

        System.out.println("raw: " + "\\u0041,\\U0001F600");
        System.out.println("unescaped: " + Util.unescape("\\u0041,\\U0001F600"));

        System.out.println();

        System.out.println("raw: " + "\\,\",\b,\f,\n,\r,\t,\\u0000,\\U00000000");
        System.out.println("escaped: " + Util.escape("\\,\",\b,\f,\n,\r,\t,\\u0000,\\U00000000", true));
    }

    /*
    public static void read() {
        System.out.println();
        System.out.println("=== READ ===");

        Reader reader = new Reader(new File("src/main/java/example/config/example.toml"));

        reader.get(
                "a_string",
                PrimitiveSerializers.getSerializer(String.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "a_multiline_string",
                PrimitiveSerializers.getSerializer(String.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "a_literal_string",
                PrimitiveSerializers.getSerializer(String.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "cat.a_boolean",
                PrimitiveSerializers.getSerializer(Boolean.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "cat.a_nan",
                PrimitiveSerializers.getSerializer(Double.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "an_infinity",
                PrimitiveSerializers.getSerializer(Double.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "a.\"23.k\".1.3",
                PrimitiveSerializers.getSerializer(boolean.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "b.d",
                PrimitiveSerializers.getSerializer(double.class).get()
        ).ifPresent(System.out::println);

        reader.get(
                "c",
                PrimitiveSerializers.getSerializer(Color.class).get()
        ).ifPresent(System.out::println);
    }

     */

    public static void load(Config config) {
        System.out.println();
        System.out.println("=== LOAD ===");

        config.load();
        System.out.println(config.c);
        System.out.println(config.s);
        System.out.println(config.test);
        System.out.println(config.testLocalized);
    }

    public static void save(Config config) {
        System.out.println();
        System.out.println("=== SAVE ===");

        config.save();
    }

    public enum Test {
        A, B, C;
    }

    public enum TestLocalized {
        A("aaa"), B("bbb"), C("ccc");

        private final String name;

        TestLocalized(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Comment("A toml generated by Pierced")
    @Comment("B")
    @Comment("C")
    @TableComment(table = "a.\"b\"", comment = "This is a table comment")
    @TableComment(table = "a.\"b\"", comment = " ")
    @TableComment(table = "a.\"b\"", comment = "This is another table comment")
    public static class Config extends Pierced {
        public Config() {
            super(Config.class, new File("src/main/java/example/config/test.toml"));
        }

        @InlineComment("INLINE")
        @Table("a . \"b\"")
        public Color c = new Color(0x172D8F);

        @Comment("DEF")
        public double aaa = 2.3;

        public String s = "abc";

        public Test test = Test.B;

        public TestLocalized testLocalized = TestLocalized.A;
    }
}
