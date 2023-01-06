package example;

import java.io.File;

public class Example {
	public static void main(String[] args) {
		Config config = new Config(new File("src/main/java/example/config/test.toml"));
		System.out.println(config.token);
		config.load();
		System.out.println(config.token);
		config.token = "xyz";
		config.save();
	}
}
