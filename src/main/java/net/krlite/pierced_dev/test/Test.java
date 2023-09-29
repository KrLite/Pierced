package net.krlite.pierced_dev.test;

import net.krlite.pierced_dev.ast.io.Reader;
import net.krlite.pierced_dev.serialization.PrimitiveSerializers;

import java.io.File;
import java.util.Objects;

public class Test {
	public static void main(String[] args) {
		Reader reader = new Reader(new File(Objects.requireNonNull(Test.class.getResource("/test/test.toml")).getFile()));

		reader.get(
				"a.\"23.k\".1.3",
				PrimitiveSerializers.BOOLEAN
		).ifPresent(System.out::println);

		reader.get(
				"b",
				PrimitiveSerializers.DOUBLE
		).ifPresent(System.out::println);
	}
}
