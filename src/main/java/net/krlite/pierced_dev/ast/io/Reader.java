package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.key.Table;
import net.krlite.pierced_dev.ast.regex.recursive.Array;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;

public class Reader {
	public static final Class<?>[] PRIMITIVE_TYPES = {
			String.class,
			Character.class, char.class,
			Boolean.class, boolean.class,
			Byte.class, byte.class,
			Short.class, short.class,
			Integer.class, int.class,
			Long.class, long.class,
			Float.class, float.class,
			Double.class, double.class
	};

	public static final Class<?>[] RECURSIVE_TYPES = {
		Array.class, Map.class
	};

	public static <T> T read(File file, String key, Class<T> expectedType) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(reader);

		final HashMap<String, String> result = new HashMap<>();
		String line, keyValuePair = "";

		while ((line = bufferedReader.readLine()) != null) {
			// Find the key
			if (keyValuePair.isEmpty()) {
				Matcher tableMatcher = Table.TABLE.matcher(line);
				Matcher keyMatcher = Key.KEY.matcher(line);

				if (!keyMatcher.find() && !tableMatcher.find()) continue;

				if (tableMatcher.find()) {
					String table = tableMatcher.group(1);
					Matcher stdTableMatcher = Table.STD_TABLE.matcher(table);
					if (stdTableMatcher.find()) {
						String stdTable = stdTableMatcher.group(1);
						if (!key.startsWith(stdTable)) {
							continue;
						}
					}
				}

				else if (keyMatcher.find()) {
					String keyName = keyMatcher.group(1);
					Matcher dottedKeyMatcher = Key.DOTTED_KEY.matcher(keyName);
					Matcher simpleKeyMatcher = Key.SIMPLE_KEY.matcher(keyName);

					if (dottedKeyMatcher.find()) {
						String dottedKey = dottedKeyMatcher.group(1);
						if (!key.equals(dottedKey)) {
							continue;
						}
					}
					else if (simpleKeyMatcher.find()) {
						String simpleKey = simpleKeyMatcher.group(1);
						if (!key.equals(simpleKey)) {
							continue;
						}
					}
				}

				keyValuePair = line;
			}
		}
	}
}
