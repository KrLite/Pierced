package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ExceptionHandler;
import net.krlite.pierced_dev.WithFile;
import net.krlite.pierced_dev.ast.regex.Comment;
import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.key.Table;
import net.krlite.pierced_dev.ast.regex.primitive.MultilineBasicString;
import net.krlite.pierced_dev.ast.regex.primitive.MultilineLiteralString;
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

	private enum State {
		NORMAL, MLB, MLL;
	}

	private State state = State.NORMAL;

	public <T> Optional<T> get(String rawKey, Deserializable<T> deserializable) {
		Optional<BufferedReader> bufferedReader = readFromFile();
		if (!bufferedReader.isPresent()) return Optional.empty();

		rawKey = Util.flatten(Util.unescape(Util.normalizeKey(rawKey)), true);
		String line;
        StringBuilder keyValuePair = new StringBuilder();

        while (true) {
			try {
				if ((line = bufferedReader.get().readLine()) == null)
					break;
			} catch (IOException e) {
				addException(ExceptionHandler.handleBufferReaderReadLineException(e));
				break;
			}

			switch (state) {
				case MLB: {
					if (MultilineBasicString.ML_BASIC_STRING_DELIM.matcher(line).find()) {
						keyValuePair.append(Util.LINE_TERMINATOR).append(line);
						state = State.NORMAL;
						break;
					}

					else {
						keyValuePair.append(Util.LINE_TERMINATOR).append(line);
						continue;
					}
				}
				case MLL: {
					if (MultilineLiteralString.ML_LITERAL_STRING_DELIM.matcher(line).find()) {
						keyValuePair.append(Util.LINE_TERMINATOR).append(line);
						state = State.NORMAL;
						break;
					}

					else {
						keyValuePair.append(Util.LINE_TERMINATOR).append(line);
						continue;
					}
				}
			}

			if (Comment.COMMENT.matcher(line).matches())
				continue;

			if (keyValuePair.length() == 0) {
				if (MultilineBasicString.ML_BASIC_STRING_DELIM.matcher(line).find()) {
					keyValuePair = new StringBuilder(line);
					state = State.MLB;

					continue;
				}

				if (MultilineLiteralString.ML_LITERAL_STRING_DELIM.matcher(line).find()) {
					keyValuePair = new StringBuilder(line);
					state = State.MLL;

					continue;
				}

				keyValuePair = new StringBuilder(line);
			}

			if (keyValuePair.length() > 0) {
				Matcher matcher = Pattern.compile("").matcher(keyValuePair);

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

							Matcher primitiveMatcher = Primitive.PRIMITIVE.matcher(keyValuePair.substring(matcher.end()));
							boolean primitiveMatched = primitiveMatcher.matches();

							if (primitiveMatched) {
								// Primitive found
								String primitiveValue = primitiveMatcher.group();

								return deserializable.deserialize(primitiveValue);
							}
						}
					}
				}

				keyValuePair = new StringBuilder();
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
