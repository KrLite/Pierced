package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.key.Table;
import net.krlite.pierced_dev.ast.regex.primitive.Primitive;
import net.krlite.pierced_dev.ast.util.NormalizeUtil;
import net.krlite.pierced_dev.serialization.base.Deserializable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {
	protected final File file;
	protected final HashMap<Long, Exception> exceptions = new HashMap<>();

	public Reader(File file) {
		this.file = file;
	}

	public File file() {
		return file;
	}

	public Map<Long, Exception> exceptions() {
		return new HashMap<>(exceptions);
	}

	protected void addException(Exception e) {
		exceptions.put(System.currentTimeMillis(), e);
	}

	public BufferedReader readFromFile() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
		return new BufferedReader(reader);
	}

	/**
	 * Gets a value by a key.
	 * @param key	The key of the target value.
	 * @param deserializable	The {@link Deserializable} to deserialize the value.
	 * @return	An {@link Optional} of the value.
	 * @param <T>	The type of the value.
	 */
	public <T> Optional<T> get(String key, Deserializable<T> deserializable) {
		BufferedReader bufferedReader = readFromFile();
		key = NormalizeUtil.normalizeKey(key);
		key = NormalizeUtil.unescapeKey(key, false);
		String line, keyValuePair = "";

		while (true) {
			try {
				if ((line = bufferedReader.readLine()) == null) break;
			} catch (IOException e) {
				addException(e);
				break;
			}
			Matcher matcher = Pattern.compile("").matcher(line);

			if (keyValuePair.isEmpty()) {
                matcher.usePattern(Table.TABLE);
                boolean tableFound = matcher.find();

				if (tableFound) {
					// Table found
					String table = matcher.group();

					Matcher stdTableMatcher = Table.STD_TABLE.matcher(table);
					boolean stdTableMatcherFound = stdTableMatcher.find();

					if (stdTableMatcherFound) {
						String stdTable = stdTableMatcher.group(1);
						if (!key.startsWith(stdTable)) {
							continue;
						}
					}
				}

                matcher.usePattern(Key.KEY);
                boolean keyFound = matcher.find();

				if (keyFound) {
					// Key found
					String normalizedKey = NormalizeUtil.normalizeKey(matcher.group());
					String localKey = NormalizeUtil.unescapeKey(normalizedKey, false);

					if (localKey.equals(key)) {
						// Key matched
						matcher.usePattern(Key.KEYVAL_SEP);
						boolean keyvalSepFound = matcher.find();

						if (keyvalSepFound) {
							// Key-val Sep found
							String keyvalSep = matcher.group();

							matcher.usePattern(Primitive.PRIMITIVE);
							boolean primitiveFound = matcher.find();

							if (primitiveFound) {
								// Primitive found
								String primitiveValue = matcher.group();
								return deserializable.deserialize(primitiveValue);
							}
						}
					}
				}
			}
		}

		try {
			bufferedReader.close();
		} catch (IOException e) {
			addException(e);
		}
		return Optional.empty();
	}
}
