package net.krlite.pierced_dev.ast.regex.primitive;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Comment;
import net.krlite.pierced_dev.ast.regex.Newline;
import net.krlite.pierced_dev.ast.regex.Whitespace;

import java.util.regex.Pattern;

public class MultilineBasicString extends ABNF {
	public static final Pattern MLB_QUOTES = repeats(BasicString.QUOTATION_MARK, 1, 2);
	public static final Pattern MLB_UNESCAPED = or(
			Whitespace.WSCHAR,
			Pattern.compile("\\x21\\x23-\\x5B\\x5D-\\x7E"),
			Comment.NON_ASCII
	);
	public static final Pattern MLB_ESCAPED_NL = chain(
			BasicString.ESCAPE,
			Whitespace.WS,
			Newline.NEWLINE,
			repeats(or(
					Whitespace.WSCHAR,
					Newline.NEWLINE
			))
	);
	public static final Pattern MLB_CHAR = or(MLB_UNESCAPED, BasicString.ESCAPED);
	public static final Pattern MLB_CONTENT = or(
			MLB_CHAR,
			Newline.NEWLINE,
			MLB_ESCAPED_NL
	);

	public static final Pattern ML_BASIC_BODY = chain(
			repeats(MLB_CONTENT),
			repeats(chain(
					MLB_QUOTES,
					repeatsAtLeast(MLB_CONTENT, 1)
			)),
			optional(MLB_QUOTES)
	);
	public static final Pattern ML_BASIC_STRING_DELIM = repeats(BasicString.QUOTATION_MARK, 3);
	public static final Pattern ML_BASIC_STRING = chain(
			ML_BASIC_STRING_DELIM,
			optional(Newline.NEWLINE),
			ML_BASIC_BODY,
			ML_BASIC_STRING_DELIM
	);
}
