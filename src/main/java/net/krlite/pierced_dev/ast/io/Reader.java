package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.key.Table;
import net.krlite.pierced_dev.ast.regex.primitive.Primitive;
import net.krlite.pierced_dev.ast.util.Util;
import net.krlite.pierced_dev.serialization.base.Deserializable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
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

	public HashMap<Long, Exception> exceptions() {
		return new HashMap<>(exceptions);
	}

	protected void addException(Exception e) {
		exceptions.put(System.currentTimeMillis(), e);
	}

	public Optional<BufferedReader> readFromFile() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file());
		} catch (FileNotFoundException e) {
			addException(new IOException("File '" + file.getName() + "' does not exist"));
			return Optional.empty();
		}

		InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
		return Optional.of(new BufferedReader(reader));
	}

	public <T> Optional<T> get(String key, Deserializable<T> deserializable) {
		Optional<BufferedReader> bufferedReader = readFromFile();
		if (!bufferedReader.isPresent()) return Optional.empty();

		key = Util.unescapeKey(Util.normalizeKey(key), true);
		String line, keyValuePair = "";

		while (true) {
			try {
				if ((line = bufferedReader.get().readLine()) == null) break;
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
						String stdTable = Util.unescapeKey(Util.normalizeStdTable(stdTableMatcher.group()), true);
						key = key.replaceFirst("^" + stdTable + ".", "");
					}
				}

                matcher.usePattern(Key.KEY);
                boolean keyFound = matcher.find();

				if (keyFound) {
					// Key found
					String localKey = Util.unescapeKey(Util.normalizeKey(matcher.group()), true);

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
			bufferedReader.get().close();
		} catch (IOException e) {
			addException(e);
		}
		return Optional.empty();
	}
}
