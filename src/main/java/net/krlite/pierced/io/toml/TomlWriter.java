package net.krlite.pierced.io.toml;

import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.annotation.Comment;
import net.krlite.pierced.core.Convertable;
import net.krlite.pierced.core.EnumLocalizable;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TomlWriter {
	private final File file;
	public final ArrayList<Exception> exceptions = new ArrayList<>();

	public TomlWriter(File file) {
		this.file = file;
		initialize();
	}

	protected void initialize() {
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException ioException) {
				exceptions.add(ioException);
			}
		} else {
			try {
				FileWriter writer = new FileWriter(file);
				writeAndClose(writer, "");
			} catch (IOException ioException) {
				exceptions.add(ioException);
			}
		}
	}

	public <C> void save(String key, Object value, Class<C> clazz) {
		if (clazz == null || value == null) return;
		if (PRIMITIVE_TYPES.contains(clazz) || WRAPPER_TYPES.contains(clazz)) {
			write(key, value.toString());
		} else if (Convertable.class.isAssignableFrom(clazz)) {
			write(key, ((Convertable<?>) value).convertToString());
		} else if (clazz.isEnum())
			write(key, quote(EnumLocalizable.class.isAssignableFrom(clazz) ?
									 ((EnumLocalizable) value).getLocalizedName() : ((Enum<?>) value).name()));
		else if (clazz == Color.class)
			write(key, quote(Integer.toHexString(((Color) value).getRGB())));
		else if (clazz == String.class) {
			String compiled = value.toString();
			if (Pattern.compile("\n").matcher(compiled).find())
				write(key, "'''\n" + compiled + "'''");
			else
				write(key, quote(compiled));
		} else write(key, quote(value.toString()));
	}

	public void table(Table table) {
		try {
			if (!table.value().isEmpty())
				write("[" + table.value() + "]");
		} catch (IOException ioException) {
			exceptions.add(ioException);
		}
	}

	public void comment(Comment comment) {
		Arrays.stream(comment.value().replaceAll("^\n", "").split("\n")).forEach(line -> {
			try {
				if (line.isEmpty()) write("");
				else write("# " + line);
			} catch (IOException ioException) {
				exceptions.add(ioException);
			}
		});
	}

	public void comment(Comment[] comments) {
		Arrays.stream(comments).forEach(this::comment);
	}

	public void typeComment(Comment comment) {
		if (comment.value().replaceAll("\n", "").isEmpty())
			return;
		comment(comment);
	}

	public void typeComment(Comment[] comments) {
		comments = Arrays.stream(comments).dropWhile(c -> c.value().replaceAll("\n", "").isEmpty()).toArray(Comment[]::new);
		if (comments.length == 0) return;
		comment(comments);
	}

	private String quote(String value) {
		return "'" + value + "'";
	}

	private boolean separate = false, ready = false;

	private void write(String line) throws IOException {
		FileWriter writer = new FileWriter(file, true);
		boolean isComment = TomlRegex.COMMENT.matcher(line).matches();
		if (ready) {
			writer.write("\n");
			if ((separate && !isComment && !line.isEmpty()) || TomlRegex.CATEGORY.matcher(line).matches())
				writer.write("\n");
		} else ready = true;
		separate = isComment;
		writeAndClose(writer, line);
	}

	private void write(String key, String value) {
		try {
			write(key + " = " + value);
		} catch (IOException ioException) {
			exceptions.add(ioException);
		}
	}

	private void writeAndClose(Writer writer, String content) throws IOException {
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static final List<Class<?>> PRIMITIVE_TYPES = Arrays.asList(
			boolean.class, byte.class, short.class, int.class, long.class, float.class, double.class
	);

	public static final List<Class<?>> WRAPPER_TYPES = Arrays.asList(
			Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class
	);
}
