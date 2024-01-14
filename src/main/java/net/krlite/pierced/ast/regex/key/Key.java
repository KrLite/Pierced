package net.krlite.pierced.ast.regex.key;

import net.krlite.pierced.ast.regex.ABNF;
import net.krlite.pierced.ast.regex.Whitespace;
import net.krlite.pierced.ast.regex.primitive.BasicString;
import net.krlite.pierced.ast.regex.primitive.LiteralString;

import java.util.regex.Pattern;

public class Key extends ABNF {
	public static final Pattern DOT_SEP = chain(repeats(Whitespace.WS), Pattern.compile("\\."), repeats(Whitespace.WS));
	public static final Pattern KEYVAL_SEP = chain(repeats(Whitespace.WS), Pattern.compile("="), repeats(Whitespace.WS));

	public static final Pattern UNQUOTED_KEY = repeatsAtLeast(or(
			ALPHA,
			DIGIT,
			Pattern.compile("-"),
			Pattern.compile("_")
	), 1);
	public static final Pattern QUOTED_KEY = or(
			BasicString.BASIC_STRING,
			LiteralString.LITERAL_STRING
	);
	public static final Pattern SIMPLE_KEY = or(
			UNQUOTED_KEY,
			QUOTED_KEY
	);
	public static final Pattern DOTTED_KEY = chain(
			SIMPLE_KEY,
			repeatsAtLeast(chain(
					DOT_SEP,
					SIMPLE_KEY
			), 1)
	);

	public static final Pattern KEY = or(
			DOTTED_KEY,
			SIMPLE_KEY
	);
}
