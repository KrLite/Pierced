package net.krlite.pierced.io.toml;

import java.util.regex.Pattern;

public class TomlRegex {
	public static final Pattern CATEGORY = Pattern.compile("^(\\[(?<category>.*)])(#.*)?$");
	public static final Pattern COMMENT = Pattern.compile("^(#.*)$");
	public static final Pattern PAIR = Pattern.compile("\\s*=\\s");
	public static final Pattern RAW_KEY = Pattern.compile("^(?<key>.+)" + PAIR.pattern());
	public static final Pattern QUOTED_KEY = Pattern.compile("^\"(?<key>.*)\"" + PAIR.pattern());
	public static final Pattern TRIPLE_QUOTED_KEY = Pattern.compile("^\"{3}(?<key>.*)\"{3}" + PAIR.pattern());
	public static final Pattern LITERAL_QUOTED_KEY = Pattern.compile("^'(?<key>.*)'" + PAIR.pattern());
	public static final Pattern LITERAL_TRIPLE_QUOTED_KEY = Pattern.compile("^'{3}(?<key>.*)'{3}" + PAIR.pattern());
	public static final Pattern RAW_VALUE = Pattern.compile(PAIR.pattern() + "(?<value>(true)|(false)|(\\+|-|)(nan|inf))\\s*$");
	public static final Pattern QUOTED_VALUE = Pattern.compile(PAIR.pattern() + "\"(?<value>.*)\"\\s*$");
	public static final Pattern TRIPLE_QUOTED_VALUE = Pattern.compile(PAIR.pattern() + "\"{3}(?<value>.*)\"{3}$");
	public static final Pattern LITERAL_QUOTED_VALUE = Pattern.compile(PAIR.pattern() + "'(?<value>.*)'\\s*$");
	public static final Pattern LITERAL_TRIPLE_QUOTED_VALUE = Pattern.compile(PAIR.pattern() + "'{3}(?<value>.*)'{3}$");
	public static final Pattern MULTILINE_VALUE_BEGIN = Pattern.compile(PAIR.pattern() + "\"{3}(?<value>.*)$");
	public static final Pattern MULTILINE_VALUE_END = Pattern.compile("(?<value>.*)\"{3}$");
	public static final Pattern MULTILINE_LITERAL_VALUE_BEGIN = Pattern.compile(PAIR.pattern() + "'{3}(?<value>.*)$");
	public static final Pattern MULTILINE_LITERAL_VALUE_END = Pattern.compile("(?<value>.*)'{3}$");
}
