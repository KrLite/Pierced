package net.krlite.pierced.io.toml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class TomlReader {
	private final File file;
	public final HashMap<String, String> content = new HashMap<>();
	public final ArrayList<Exception> exceptions = new ArrayList<>();

	public TomlReader(File file) {
		this.file = file;
	}

	public TomlReader queue() {
		exceptions.clear();
		try {
			content.clear();
			content.putAll(read());
		} catch (IOException exception) {
			exceptions.add(exception);
		}
		return this;
	}

	public ArrayList<Exception> exceptions() {
		return exceptions;
	}

	public HashMap<String, String> read() throws IOException {
		final HashMap<String, String> result = new HashMap<>();
		FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(reader);

		String line, category = "";

		while((line = bufferedReader.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			}

			String key = "";
			String value = "";
			Matcher matcher;

			// Comment
			if (TomlRegex.COMMENT.matcher(line).find()) {
			} // Category
			else if ((matcher = TomlRegex.CATEGORY.matcher(line)).find()) {
				category = matcher.group("category").trim() + ".";
			} // Valid key-value pair
			else if ((matcher = TomlRegex.QUOTED_KEY.matcher(line)).find() || (matcher = TomlRegex.TRIPLE_QUOTED_KEY.matcher(line)).find() ||
							 (matcher = TomlRegex.LITERAL_QUOTED_KEY.matcher(line)).find() || (matcher = TomlRegex.LITERAL_TRIPLE_QUOTED_KEY.matcher(line)).find() ||
							 (matcher = TomlRegex.RAW_KEY.matcher(line)).find()) {
				key = category + matcher.group("key");
				// Valid multiline value
				if ((matcher = TomlRegex.MULTILINE_VALUE_BEGIN.matcher(line)).find()) {
					StringBuilder builder = new StringBuilder(matcher.group("value"));
					while ((line = bufferedReader.readLine()) != null) {
						if ((matcher = TomlRegex.MULTILINE_VALUE_END.matcher(line)).find()) {
							builder.append(matcher.group("value"));
							break;
						} else {
							builder.append(line);
						}
					}
					value = builder.toString();
				} // Valid literal multiline value
				else if ((matcher = TomlRegex.MULTILINE_LITERAL_VALUE_BEGIN.matcher(line)).find()) {
					StringBuilder builder = new StringBuilder(matcher.group("value"));
					while ((line = bufferedReader.readLine()) != null) {
						if ((matcher = TomlRegex.MULTILINE_LITERAL_VALUE_END.matcher(line)).find()) {
							builder.append(matcher.group("value"));
							break;
						} else {
							builder.append(line);
						}
					}
					value = builder.toString();
				} // Valid value
				else if ((matcher = TomlRegex.QUOTED_VALUE.matcher(line)).find() || (matcher = TomlRegex.TRIPLE_QUOTED_VALUE.matcher(line)).find() ||
								 (matcher = TomlRegex.LITERAL_QUOTED_VALUE.matcher(line)).find() || (matcher = TomlRegex.LITERAL_TRIPLE_QUOTED_VALUE.matcher(line)).find() ||
								 (matcher = TomlRegex.RAW_VALUE.matcher(line)).find()) {
					value = matcher.group("value");
				}
				else {
					continue;
				}
			}

			key = key.replaceAll("\\s*", "");

			if (!key.isEmpty()) {
				result.put(key, value);
			}
		}

		bufferedReader.close();
		reader.close();
		return result;
	}
}
