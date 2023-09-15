package net.krlite.pierced_dev.test;

import net.krlite.pierced_dev.ast.io.Reader;

import java.io.File;
import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		try {
			System.out.println(Reader.read(new File("test.toml"), "a", boolean.class));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
