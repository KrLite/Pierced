package net.krlite.pierced.io.toml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Strings
// TODO: Multiline strings
// TODO: Literal strings
// TODO: Multiline literal strings
// TODO: Decimal, Hex, Octal, Binary, Float numbers
// TODO: Dates and times
// TODO: Arrays
// TODO: Tables

class TomlParser {
	public static String parse(String raw) {
		String value = raw;
		// Boolean
		if (RAWABLE.matcher(value).matches()) {
			return value;
		} // Basic string
		else if (value.startsWith("\"") && value.endsWith("\"")) {
			if (value.startsWith("\"\"\"") && value.endsWith("\"\"\"")) {
				return value.substring(3, value.length() - 3);
			} else {
				return value.substring(1, value.length() - 1);
			}
		} // Literal string
		else if (value.startsWith("'") && value.endsWith("'")) {
			if (value.startsWith("'''") && value.endsWith("'''")) {
				return value.substring(3, value.length() - 3);
			} else {
				return value.substring(1, value.length() - 1);
			}
		} else {
			return value;
		}
	}

	private static String unEscape(String value) {
		Matcher unicodeMatcher = UNICODE.matcher(value);
		while (unicodeMatcher.find()) {
			String unicode = unicodeMatcher.group(2);
			value = value.replace(unicodeMatcher.group(1), (char) Integer.parseInt(unicode, 16) + "");
		}
		return value;
	}

	private static final Pattern UNICODE = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
	private static final Pattern RAWABLE = Pattern.compile("^(true|false|(\\+|-?nan)|(\\+|-?inf))$");
	private static final Pattern BASIC_STRING = Pattern.compile("\"([^\"]+)\"|'([^']+)'");
	private static final Pattern COMMENT = Pattern.compile("(#.*$)");
}
