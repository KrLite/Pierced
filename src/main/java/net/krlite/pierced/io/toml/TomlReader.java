package net.krlite.pierced.io.toml;

import net.krlite.pierced.core.Convertable;
import net.krlite.pierced.core.EnumLocalizable;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.krlite.pierced.io.toml.TomlRegex.*;

public class TomlReader {
	public final HashMap<String, String> content = new HashMap<>();
	public final ArrayList<Exception> exceptions = new ArrayList<>();

	public TomlReader(File file) {
		if (!file.exists()) {
			exceptions.add(new IOException("File " + file.getName() + " does not exist"));
			return;
		}
		try {
			content.putAll(read(file));
		} catch (IOException ioException) {
			exceptions.add(ioException);
		}
	}

	public <C, I> void load(Field field, Object value, Class<C> clazz, I instance) {
		if (clazz == null || value == null) return;
		field.setAccessible(true);
		try {
			if (clazz == Boolean.class || clazz == boolean.class)
				field.set(instance, Boolean.parseBoolean(value.toString()));
			else if (Convertable.class.isAssignableFrom(clazz))
				field.set(instance, ((Convertable<?>) value).convertFromString(value.toString()));
			else if (field.get(instance) instanceof Number) {
				String compiled = value.toString().replaceAll("_", "");
				if (clazz == Byte.class || clazz == byte.class)
					field.set(instance, Byte.parseByte(compiled));
				else if (clazz == Short.class || clazz == short.class)
					field.set(instance, Short.decode(compiled));
				else if (clazz == Integer.class || clazz == int.class)
					field.set(instance, Integer.decode(compiled));
				else if (clazz == Long.class || clazz == long.class)
					field.set(instance, Long.decode(compiled));
				else if (clazz == Float.class || clazz == float.class)
					field.set(instance, Float.parseFloat(compiled));
				else if (clazz == Double.class || clazz == double.class)
					field.set(instance, Double.parseDouble(compiled));
			} else if (clazz.isEnum())
				Arrays.stream(clazz.getEnumConstants())
						.filter(e -> EnumLocalizable.class.isAssignableFrom(clazz) ?
											 ((EnumLocalizable) e).getLocalizedName().equals(value.toString()) : ((Enum<?>) e).name().equals(value.toString()))
						.findFirst().ifPresent(e -> {
							try {
								field.set(instance, e);
							} catch (IllegalAccessException illegalAccessException) {
								exceptions.add(illegalAccessException);
							}
						});
			else if (clazz == Color.class)
				field.set(instance, Color.decode(value.toString()));
			else exceptions.add(new IllegalArgumentException("Unsupported type " + clazz.getName()));
		} catch (IllegalAccessException illegalAccessException) {
			exceptions.add(illegalAccessException);
		}
	}

	private HashMap<String, String> read(File file) throws IOException {
		final HashMap<String, String> result = new HashMap<>();
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(reader);

		String line, category = "";

		while((line = bufferedReader.readLine()) != null) {
			if (line.isEmpty()) continue;
			String key = "";
			String value = "";
			Matcher matcher = COMMENT.matcher(line);

			// Comment
			if (matcher.find()) continue;
			// Category
			else if (matcher.usePattern(CATEGORY).matches())
				category = matcher.group("category").trim() + ".";
			// Valid key-value pair
			else if (matcher.usePattern(KV_RAW).matches() ||
							 matcher.usePattern(KV_Q).matches() || matcher.usePattern(KV_Q_L).matches() ||
							 matcher.usePattern(KV_3Q).matches() || matcher.usePattern(KV_3Q_L).matches()
			) {
				key = category + matcher.group("key");
				value = matcher.group("value");
				// Valid non-literal multiline value
				if (matcher.reset(value).usePattern(MV_BEGIN).matches())
					value = readMultiline(bufferedReader, matcher, MV_END);
				// Valid literal multiline value
				else if (matcher.reset(value).usePattern(MV_L_BEGIN).matches())
					value = readMultiline(bufferedReader, matcher, MV_L_END);
				// Valid value
				else if (matcher.usePattern(V_RAW).matches() ||
							matcher.usePattern(V_Q).matches() || matcher.usePattern(V_Q_L).matches() ||
							matcher.usePattern(V_3Q).matches() || matcher.usePattern(V_3Q_L).matches()
				) {
					value = matcher.group("value");
				} else continue;
			}

			key = key.replaceAll("\\s*", "");

			if (!key.isEmpty())
				result.put(key, value);
		}

		bufferedReader.close();
		reader.close();
		return result;
	}

	private String readMultiline(BufferedReader bufferedReader, Matcher matcher, Pattern end) throws IOException {
		StringBuilder value;
		String line;
		// Value without line break
		String possible = matcher.group("value");
		if (matcher.reset(possible).usePattern(MV_NLB).matches())
			value = new StringBuilder(matcher.group("value"));
		// Value with line break
		else
			value = new StringBuilder(possible + "\n");
		while (true) {
			line = bufferedReader.readLine();
			if (line == null) {
				exceptions.add(new IOException("Unexpected end of file"));
				break;
			} // Value end
			else if (matcher.reset(line).usePattern(end).matches()) {
				value.append(matcher.group("value"));
				break;
			} // Value with line break
			else {
				possible = line + "\n";
				// Value without line break
				if (matcher.usePattern(MV_NLB).matches())
					possible = matcher.group("value");
				value.append(possible);
			}
		}
		return value.toString();
	}
}
