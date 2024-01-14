package net.krlite.pierced.ast.regex;

import java.util.regex.Pattern;

public class Comment extends ABNF {
	public static final Pattern COMMENT_START_SYMBOL = Pattern.compile("#");
	public static final Pattern NON_ASCII = Pattern.compile("[\\x80-\\uD7FF\\uE000-\\uFFFF]");
	public static final Pattern NON_EOL = join(NON_ASCII, Pattern.compile("[\\x09\\x20-\\x7F]"));

	public static final Pattern COMMENT = chain(repeats(Whitespace.WS), COMMENT_START_SYMBOL, repeats(NON_EOL));
}
