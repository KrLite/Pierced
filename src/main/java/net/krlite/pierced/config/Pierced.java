package net.krlite.pierced.config;

import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.io.toml.TomlReader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Pierced<S> {
	protected @Silent Class<S> clazz;
	protected @Silent File file;

	public Pierced(Class<S> clazz, File file) {
		this.clazz = clazz;
		this.file = file;
	}

	public void load() {
		TomlReader reader = new TomlReader(file).queue();
		Field[] fields = clazz.getDeclaredFields();
		Arrays.stream(fields).filter(Pierced::isValid)
				.forEach(field -> {
					field.setAccessible(true);
					try {
						field.set(this, reader.content.get(field.getName()));
					} catch (IllegalAccessException illegalAccessException) {
						illegalAccessException.printStackTrace();
					}
				});
	}

	public void save() {
		HashMap<String, String> content = new HashMap<>();
		Field[] fields = clazz.getDeclaredFields();
		Arrays.stream(fields).filter(Pierced::isValid)
				.forEach(field -> {
					field.setAccessible(true);
					try {
						content.put(field.getName(), (String) field.get(this));
					} catch (IllegalAccessException illegalAccessException) {
						illegalAccessException.printStackTrace();
					}
				});
		System.out.println(content);
	}

	private static boolean isValid(Field field) {
		return !field.isAnnotationPresent(Silent.class);
	}
}
