package net.krlite.pierced.ast.io;

import net.krlite.pierced.ExceptionHandler;
import net.krlite.pierced.WithFile;
import net.krlite.pierced.ast.regex.Comment;
import net.krlite.pierced.ast.regex.key.Key;
import net.krlite.pierced.ast.regex.key.Table;
import net.krlite.pierced.ast.regex.primitive.MultilineBasicString;
import net.krlite.pierced.ast.regex.primitive.MultilineLiteralString;
import net.krlite.pierced.ast.regex.primitive.Primitive;
import net.krlite.pierced.ast.regex.recursive.Array;
import net.krlite.pierced.ast.regex.recursive.InlineTable;
import net.krlite.pierced.ast.util.Util;
import net.krlite.pierced.serialization.base.Serializer;

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
		NORMAL, ARRAY, INLINE_TABLE, MLB, MLL;
	}

	private int countParseResults(String string, Pattern pattern) {
		Matcher matcher = pattern.matcher(string);
		int count = 0;

		while (matcher.find()) count++;

		return count;
	}

	public <S> Optional<S> get(String rawKey, Serializer.Wrapper<S> wrapper) {
		State state = State.NORMAL;
		Optional<BufferedReader> bufferedReader = readFromFile();
		if (!bufferedReader.isPresent()) return Optional.empty();

		rawKey = Util.flatten(Util.unescape(Util.normalizeKey(rawKey)), true);
		String line;
        StringBuilder keyValuePair = new StringBuilder();

		int parentheses = 0;

        while (true) {
			try {
				if ((line = bufferedReader.get().readLine()) == null)
					break;
			} catch (IOException e) {
				addException(ExceptionHandler.handleBufferReaderReadLineException(e));
				break;
			}

			switch (state) {
				case ARRAY: {
					if (Comment.COMMENT.matcher(line).matches()) continue;

					parentheses += countParseResults(line, Array.ARRAY_OPEN);
					parentheses -= countParseResults(line, Array.ARRAY_CLOSE);

					keyValuePair.append(line);

					if (parentheses == 0) {
						state = State.NORMAL;
						break;
					}

					else if (parentheses < 0) {
						addException(new IllegalArgumentException("Illegal array definition"));
						return Optional.empty();
					}

					else continue;
				}
				case INLINE_TABLE: {
					if (Comment.COMMENT.matcher(line).matches()) continue;

					parentheses += countParseResults(line, InlineTable.INLINE_TABLE_OPEN);
					parentheses -= countParseResults(line, InlineTable.INLINE_TABLE_CLOSE);

					keyValuePair.append(line);

					if (parentheses == 0) {
						state = State.NORMAL;
						break;
					}

					else if (parentheses < 0) {
						addException(new IllegalArgumentException("Illegal inline table definition"));
						return Optional.empty();
					}

					else continue;
				}
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

			if (Comment.COMMENT.matcher(line).matches()) continue;

			if (keyValuePair.length() == 0) {
				// ARRAY case starter
				if (Array.ARRAY_KEYVAL_STARTER.matcher(line).find()) {
					parentheses += countParseResults(line, Array.ARRAY_OPEN);
					parentheses -= countParseResults(line, Array.ARRAY_CLOSE);

					keyValuePair = new StringBuilder(line);

					if (parentheses > 0) {
						state = State.ARRAY;
						continue;
					}
				}

				// INLINE_TABLE case starter
				if (InlineTable.INLINE_TABLE_KEYVAL_STARTER.matcher(line).find()) {
					parentheses += countParseResults(line, InlineTable.INLINE_TABLE_OPEN);
					parentheses -= countParseResults(line, InlineTable.INLINE_TABLE_CLOSE);

					keyValuePair = new StringBuilder(line);

					if (parentheses > 0) {
						state = State.INLINE_TABLE;
						continue;
					}
				}

				// MLB case starter
				if (MultilineBasicString.ML_BASIC_STRING_DELIM.matcher(line).find()) {
					keyValuePair = new StringBuilder(line);
					state = State.MLB;

					continue;
				}

				// MLL case starter
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

						rawKey = rawKey.replaceFirst(Util.escapeForRegex(stdTable + "."), "");
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
							String value = keyValuePair.substring(matcher.end());

							Matcher primitiveMatcher = Primitive.PRIMITIVE_WITH_STARTER.matcher(value);
							boolean primitiveMatched = primitiveMatcher.find();

							if (primitiveMatched) {
								// Primitive found
								String primitiveValue = primitiveMatcher.group();
								System.out.println(primitiveValue);

								return wrapper.serializer().deserialize(wrapper.sClass(), primitiveValue);
							}

							if (Array.ARRAY_VALUE_STARTER.matcher(value).find()) {
								// Array found
								return wrapper.serializer().deserialize(wrapper.sClass(), value);
							}

							if (InlineTable.INLINE_TABLE_VALUE_STARTER.matcher(value).find()) {
								// Inline table found
								// TODO: 2024/1/6 Support custom inline tables
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
