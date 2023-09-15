package net.krlite.pierced_dev.ast.regex.primitive;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Comment;
import net.krlite.pierced_dev.ast.regex.Whitespace;

import java.util.regex.Pattern;

public class BasicString extends ABNF {
	public static final Pattern QUOTATION_MARK = Pattern.compile("\"");

	public static final Pattern ESCAPE = Pattern.compile("\\\\");
	public static final Pattern ESCAPE_SEQ_CHAR = join(
			Pattern.compile("[\\\\\"/bfnrt]"),
			chain(
					C('u'),
					repeats(HEXDIG, 4)
			),
			chain(
					C('U'),
					repeats(HEXDIG, 8)
			)
	); // \ " b f n r t uXXXX UXXXXXXXX

	public static final Pattern ESCAPED = chain(ESCAPE, ESCAPE_SEQ_CHAR);
	public static final Pattern BASIC_UNESCAPED = or(
			Whitespace.WSCHAR,
			Pattern.compile("\\x21\\x23-\\x5B\\x5D-\\x7E"),
			Comment.NON_ASCII
	);
	public static final Pattern BASIC_CHAR = or(BASIC_UNESCAPED, ESCAPED);

	public static final Pattern BASIC_STRING = chain(
			QUOTATION_MARK,
			repeats(BASIC_CHAR),
			QUOTATION_MARK
	);
}
