package example;

import net.krlite.pierced_dev.Pierced;
import net.krlite.pierced_dev.annotation.Table;
import net.krlite.pierced_dev.ast.io.Reader;
import net.krlite.pierced_dev.serialization.PrimitiveSerializers;

import java.awt.*;
import java.io.File;

public class Example {
    public static void main(String[] args) {
        System.out.println("=== READ ===");
        Reader reader = new Reader(new File("src/main/java/example/config/example.toml"));

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

        System.out.println("=== LOAD ===");
        Config config = new Config();
        config.load();
        System.out.println(config.exceptions());
        System.out.println(config.c);

        System.out.println("=== SAVE ===");
    }

    public static class Config extends Pierced {
        public Config() {
            super(Config.class, new File("src/main/java/example/config/test.toml"));
        }

        @Table("a . b")
        public Color c;
    }
}
