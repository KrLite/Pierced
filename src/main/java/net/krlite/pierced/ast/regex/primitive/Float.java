package net.krlite.pierced.ast.regex.primitive;

import net.krlite.pierced.ast.regex.ABNF;

import java.util.regex.Pattern;

public class Float extends ABNF {
	public static final Pattern INF = Pattern.compile("inf");
	public static final Pattern NAN = Pattern.compile("nan");
	public static final Pattern SPECIAL_FLOAT = chain(
			optional(or(
					Integer.MINUS,
					Integer.PLUS
			)),
			or(
					INF,
					NAN
			)
	);

	public static final Pattern ZERO_PREFIXABLE_INT = chain(
			DIGIT,
			repeats(or(
					DIGIT,
					chain(
							Integer.UNDERSCORE,
							DIGIT
					)
			))
	);
	public static final Pattern DECIMAL_POINT = Pattern.compile("\\.");
	public static final Pattern FRAC = chain(
			DECIMAL_POINT,
			ZERO_PREFIXABLE_INT
	);
	public static final Pattern FLOAT_INT_PART = Integer.DEC_INT;

	public static final Pattern FLOAT_EXP_PART = chain(
			optional(or(
					Integer.MINUS,
					Integer.PLUS
			)),
			ZERO_PREFIXABLE_INT
	);
	public static final Pattern EXP = chain(
			c('e'),
			FLOAT_EXP_PART
	);

	public static final Pattern FLOAT = or(
			chain(
					FLOAT_INT_PART,
					or(
							EXP,
							chain(
									FRAC,
									optional(EXP)
							)
					)
			),
			SPECIAL_FLOAT
	);
}
