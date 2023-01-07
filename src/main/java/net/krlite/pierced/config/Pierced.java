package net.krlite.pierced.config;

import net.krlite.pierced.annotation.Category;
import net.krlite.pierced.annotation.Comment;
import net.krlite.pierced.annotation.Comments;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.io.toml.TomlReader;
import net.krlite.pierced.io.toml.TomlWriter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Pierced {
	protected @Silent Class<?> clazz;
	protected @Silent File file;

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
					if (field.isAnnotationPresent(Category.class))
						key = field.getAnnotation(Category.class).value() + "." + key;
					if (reader.content.containsKey(key))
						reader.load(field, reader.content.get(key), field.getType(), this);
					else new Exception("Key " + key + " not found in " + file.getName()).printStackTrace();
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
				.collect(Collectors.groupingBy(f -> f.getAnnotation(Category.class)))
				.forEach((category, fieldList) -> {
					writer.category(category);
					fieldList.forEach(field -> saveField(writer, field));
				});
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
			illegalAccessException.printStackTrace();
		}
	}

	private static boolean isValid(Field field) {
		return !field.isAnnotationPresent(Silent.class);
	}

	private static boolean isCategorized(Field field) {
		return field.isAnnotationPresent(Category.class);
	}
}
