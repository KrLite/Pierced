package net.krlite.pierced.ast.regex.primitive;

import net.krlite.pierced.ast.regex.ABNF;

import java.util.regex.Pattern;

public class Integer extends ABNF {
	public static final Pattern MINUS = Pattern.compile("-");
	public static final Pattern PLUS = Pattern.compile("\\+");
	public static final Pattern UNDERSCORE = Pattern.compile("_");
	public static final Pattern DIGIT1_9 = Pattern.compile("[1-9]");
	public static final Pattern DIGIT0_7 = Pattern.compile("[0-7]");
	public static final Pattern DIGIT0_1 = Pattern.compile("[0-1]");

	public static final Pattern HEX_PREFIX = chain(Pattern.compile("0"), c('x'));
	public static final Pattern OCT_PREFIX = chain(Pattern.compile("0"), c('o'));
	public static final Pattern BIN_PREFIX = chain(Pattern.compile("0"), c('b'));

	public static final Pattern UNSIGNED_DEC_INT = or(
			DIGIT,
			chain(
					DIGIT1_9,
					repeatsAtLeast(or(
							DIGIT,
							chain(
									UNDERSCORE,
									DIGIT
							)
					), 1)
			)
	);
	public static final Pattern DEC_INT = chain(
			optional(or(
					MINUS,
					PLUS
			)),
			UNSIGNED_DEC_INT
	);

	public static final Pattern HEX_INT = chain(
			HEX_PREFIX,
			HEXDIG,
			repeats(or(
					HEXDIG,
					chain(
							UNDERSCORE,
							HEXDIG
					)
			))
	);
	public static final Pattern OCT_INT = chain(
			OCT_PREFIX,
			DIGIT0_7,
			repeats(or(
					DIGIT0_7,
					chain(
							UNDERSCORE,
							DIGIT0_7
					)
			))
	);
	public static final Pattern BIN_INT = chain(
			BIN_PREFIX,
			DIGIT0_1,
			repeats(or(
					DIGIT0_1,
					chain(
							UNDERSCORE,
							DIGIT0_1
					)
			))
	);

	public static final Pattern INTEGER = or(
			HEX_INT,
			OCT_INT,
			BIN_INT,
			DEC_INT
	);
}
