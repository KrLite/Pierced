package net.krlite.pierced_dev.ast.regex.recursive;

import net.krlite.pierced_dev.ast.regex.*;
import net.krlite.pierced_dev.ast.regex.key.Key;

import java.util.regex.Pattern;

public class Array extends ABNF {
	public static final Pattern ARRAY_OPEN = Pattern.compile("\\[");
	public static final Pattern ARRAY_CLOSE = Pattern.compile("]");
	public static final Pattern ARRAY_SEP = Pattern.compile(",");

	public static final Pattern ARRAY_KEYVAL_STARTER = chain(
			Pattern.compile("^"),
			repeats(Whitespace.WS),
			Key.KEY,
			Key.KEYVAL_SEP,
			ARRAY_OPEN
	);
	public static final Pattern ARRAY_VALUE_STARTER = chain(
			Pattern.compile("^"),
			ARRAY_OPEN
	);
	public static final Pattern ARRAY_LAYER = chain(
			Pattern.compile("^"),
			repeats(Whitespace.WS),
			ARRAY_OPEN,
			group("value", Pattern.compile(".*")),
			ARRAY_CLOSE,
			repeats(Whitespace.WS),
			Pattern.compile("$")
	);

	public static final Pattern WS_COMMENT_NEWLINE = repeats(or(
			Whitespace.WSCHAR,
			chain(
					optional(Comment.COMMENT),
					NewLine.NEWLINE
			)
	));
}
