package net.krlite.pierced.config;

import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.annotation.Comment;
import net.krlite.pierced.annotation.Comments;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.io.toml.TomlReader;
import net.krlite.pierced.io.toml.TomlWriter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Pierced {
	protected @Silent Class<?> clazz;
	protected @Silent File file;
	private final @Silent ArrayList<Exception> exceptions = new ArrayList<>();

	public Pierced(Class<?> clazz, File file) {
		this.clazz = clazz;
		this.file = file;
	}

	public void load() {
		TomlReader reader = new TomlReader(file);
		Field[] fields = clazz.getDeclaredFields();
		Arrays.stream(fields).filter(Pierced::isValid)
				.forEach(field -> {
					String key = field.getName();
					if (field.isAnnotationPresent(Table.class))
						key = field.getAnnotation(Table.class).value() + "." + key;
					if (reader.content.containsKey(key))
						reader.load(field, reader.content.get(key), field.getType(), this);
					else exceptions.add(new Exception("Key '" + key + "' not found in " + file.getName()));
				});
	}

	public void save() {
		TomlWriter writer = new TomlWriter(file);
		// Type comments
		if (clazz.isAnnotationPresent(Comment.class))
			writer.typeComment(clazz.getAnnotation(Comment.class));
		else if (clazz.isAnnotationPresent(Comments.class))
			writer.typeComment(clazz.getAnnotation(Comments.class).value());

		Field[] fields = Arrays.stream(clazz.getDeclaredFields()).filter(Pierced::isValid).toArray(Field[]::new);
		// Uncategorized fields
		Arrays.stream(fields).filter(f -> !isCategorized(f))
				.forEach(field -> saveField(writer, field));
		// Categorized fields
		Arrays.stream(fields).filter(Pierced::isCategorized)
				.collect(Collectors.groupingBy(f -> f.getAnnotation(Table.class)))
				.forEach((table, fieldList) -> {
					writer.table(table);
					fieldList.forEach(field -> saveField(writer, field));
				});
	}

	public ArrayList<Exception> getExceptions() {
		return exceptions;
	}

	public void printExceptions() {
		exceptions.forEach(Exception::printStackTrace);
	}

	private void saveField(TomlWriter writer, Field field) {
		field.setAccessible(true);
		try {
			writer.save(field.getName(), field.get(this), field.getType());
			// Field comments
			if (field.isAnnotationPresent(Comment.class))
				writer.comment(field.getAnnotation(Comment.class));
			else if (field.isAnnotationPresent(Comments.class))
				writer.comment(field.getAnnotation(Comments.class).value());
		} catch (IllegalAccessException illegalAccessException) {
			exceptions.add(illegalAccessException);
		}
	}

	private static boolean isValid(Field field) {
		return !field.isAnnotationPresent(Silent.class);
	}

	private static boolean isCategorized(Field field) {
		return field.isAnnotationPresent(Table.class);
	}
}
