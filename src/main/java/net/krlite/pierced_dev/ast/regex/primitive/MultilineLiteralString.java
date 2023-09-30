package net.krlite.pierced_dev.ast.regex.primitive;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Comment;
import net.krlite.pierced_dev.ast.regex.NewLine;

import java.util.regex.Pattern;

public class MultilineLiteralString extends ABNF {
	public static final Pattern MLL_QUOTES = repeats(LiteralString.APOSTROPHE, 1, 2);
	public static final Pattern MLL_CHAR = or(
			Pattern.compile("[\\x09\\x20-\\x26\\x28-\\x7E]"),
			Comment.NON_ASCII
	);
	public static final Pattern MLL_CONTENT = or(
			MLL_CHAR,
			NewLine.NEWLINE
	);

	public static final Pattern ML_LITERAL_BODY = chain(
			repeats(MLL_CONTENT),
			repeats(chain(
					MLL_QUOTES,
					repeatsAtLeast(MLL_CONTENT, 1)
			)),
			optional(MLL_QUOTES)
	);
	public static final Pattern ML_LITERAL_STRING_DELIM = repeats(LiteralString.APOSTROPHE, 3);
	public static final Pattern ML_LITERAL_STRING = chain(
			ML_LITERAL_STRING_DELIM,
			optional(NewLine.NEWLINE),
			ML_LITERAL_BODY,
			ML_LITERAL_STRING_DELIM
	);
}
