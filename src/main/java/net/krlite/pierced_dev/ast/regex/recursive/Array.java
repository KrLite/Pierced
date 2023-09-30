package net.krlite.pierced_dev.ast.regex.recursive;

import net.krlite.pierced_dev.ast.regex.*;

import java.util.regex.Pattern;

public class Array<C> extends ABNF {
	public static final Pattern ARRAY_OPEN = Pattern.compile("\\[");
	public static final Pattern ARRAY_CLOSE = Pattern.compile("]");
	public static final Pattern ARRAY_SEP = Pattern.compile(",");

	public static final Pattern WS_COMMENT_NEWLINE = repeats(or(
			Whitespace.WSCHAR,
			chain(
					optional(Comment.COMMENT),
					NewLine.NEWLINE
			)
	));
}
