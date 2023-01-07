package net.krlite.pierced.io.toml;

import java.util.regex.Pattern;

public class TomlRegex {
	public static final Pattern CATEGORY = Pattern.compile("^(\\[(?<category>.*)])(#.*)?$");
	public static final Pattern COMMENT = Pattern.compile("(#.*)");
	public static final Pattern VALUE = Pattern.compile("(?<value>.+)");
	public static final Pattern PAIR = Pattern.compile("\\s*=\\s");

	// Key-value pairs
	public static final Pattern KV_RAW = Pattern.compile("(?<key>.+)" + PAIR.pattern() + VALUE.pattern() + COMMENT.pattern() + "?");
	public static final Pattern KV_Q = Pattern.compile("\"(?<key>.*)\"" + PAIR.pattern() + VALUE.pattern() + COMMENT.pattern() + "?");
	public static final Pattern KV_3Q = Pattern.compile("\"{3}(?<key>.*)\"{3}" + PAIR.pattern() + VALUE.pattern() + COMMENT.pattern() + "?");
	public static final Pattern KV_Q_L = Pattern.compile("'(?<key>.*)'" + PAIR.pattern() + VALUE.pattern() + COMMENT.pattern() + "?");
	public static final Pattern KV_3Q_L = Pattern.compile("'{3}(?<key>.*)'{3}" + PAIR.pattern() + VALUE.pattern() + COMMENT.pattern() + "?");

	// Values
	public static final Pattern V_RAW =
			Pattern.compile(
					"(?<value>" +
							"(true)|(false)" + "|" + // Boolean
							"(\\+|-|)(nan|inf|[0-9_.e]+)" + "|" + // Number
							"(0b[0-1]+)" + "|" + // Binary
							"(0o[0-7]+)" + "|" + // Octal
							"(0x[0-9a-fA-F]+)" + // Hexadecimal
							")" + COMMENT.pattern() + "?"
			);
	public static final Pattern V_Q = Pattern.compile("\"(?<value>.*)\"" + COMMENT.pattern() + "?");
	public static final Pattern V_3Q = Pattern.compile("\"{3}(?<value>.*)\"{3}" + COMMENT.pattern() + "?");
	public static final Pattern V_Q_L = Pattern.compile("'(?<value>.*)'" + COMMENT.pattern() + "?");
	public static final Pattern V_3Q_L = Pattern.compile("'{3}(?<value>.*)'{3}" + COMMENT.pattern() + "?");

	// Multiline values
	public static final Pattern MV_BEGIN = Pattern.compile("\"{3}(?<value>.*(?!\"{3}))");
	public static final Pattern MV_END = Pattern.compile("(?<value>.*)\"{3}" + COMMENT.pattern() + "?");
	public static final Pattern MV_L_BEGIN = Pattern.compile(PAIR.pattern() + "'{3}(?<value>.*(?!'{3}))");
	public static final Pattern MV_L_END = Pattern.compile("(?<value>.*)'{3}" + COMMENT.pattern() + "?");
	public static final Pattern MV_NLB = Pattern.compile("(?<value>.*)\\\\");
}
