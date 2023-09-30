package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ExceptionHandler;
import net.krlite.pierced_dev.WithFile;
import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.key.Table;
import net.krlite.pierced_dev.ast.regex.primitive.Primitive;
import net.krlite.pierced_dev.ast.util.Util;
import net.krlite.pierced_dev.serialization.base.Deserializable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader extends WithFile {
	public Reader(File file) {
		super(file);
	}

	@Override
	public File file() {
		return super.file();
	}

	public Optional<BufferedReader> readFromFile() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file());
		} catch (FileNotFoundException e) {
			addException(ExceptionHandler.handleFileDoesNotExistException(e, file().getName()));
			return Optional.empty();
		}

		InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
		return Optional.of(new BufferedReader(reader));
	}

	public <T> Optional<T> get(String rawKey, Deserializable<T> deserializable) {
		Optional<BufferedReader> bufferedReader = readFromFile();
		if (!bufferedReader.isPresent()) return Optional.empty();

		rawKey = Util.flatten(Util.unescape(Util.normalizeKey(rawKey)), true);
		String line, keyValuePair = "";

		while (true) {
			try {
				if ((line = bufferedReader.get().readLine()) == null) break;
			} catch (IOException e) {
				addException(ExceptionHandler.handleBufferReaderReadLineException(e));
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
						String stdTable = stdTableMatcher.group();
						stdTable = Util.flatten(Util.unescape(Util.normalizeStdTable(stdTable)), true);

						rawKey = rawKey.replaceFirst("^" + stdTable + ".", "");
					}
				}

                matcher.usePattern(Key.KEY);
                boolean keyFound = matcher.find();

				if (keyFound) {
					// Key found
					String key = matcher.group();
					key = Util.flatten(Util.unescape(Util.normalizeKey(key)), true);

					if (key.equals(rawKey)) {
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
			addException(ExceptionHandler.handleBufferReaderCloseException(e));
		}
		return Optional.empty();
	}
}
