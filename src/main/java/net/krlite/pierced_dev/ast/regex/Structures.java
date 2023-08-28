package net.krlite.pierced_dev.ast.regex;

import java.util.regex.Pattern;

public final class Structures extends ABNF {
	// Region	Whitespace
	public static final Pattern WSCHAR = Pattern.compile("[ \\t]");
	public static final Pattern WS = repeats(WSCHAR);
	// End



	// Region	Newline
	public static final Pattern NEWLINE = Pattern.compile("(\\r\\n)|(\\n)|(\\r)");
	// End



	// Region	Comment
	public static final Pattern COMMENT_START_SYMBOL = Pattern.compile("#");
	public static final Pattern NON_ASCII = Pattern.compile("[\\x80-\\uD7FF\\uE000-\\uFFFF]");
	public static final Pattern NON_EOL = join(NON_ASCII, Pattern.compile("[\\x09\\x20-\\x7F]"));

	public static final Pattern COMMENT = chain(COMMENT_START_SYMBOL, repeats(NON_EOL));
	// End



	// Region	Basic String
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
			WSCHAR,
			Pattern.compile("\\x21\\x23-\\x5B\\x5D-\\x7E"),
			NON_ASCII
	);
	public static final Pattern BASIC_CHAR = or(BASIC_UNESCAPED, ESCAPED);

	public static final Pattern BASIC_STRING = chain(
			QUOTATION_MARK,
			repeats(BASIC_CHAR),
			QUOTATION_MARK
	);
	// End



	// Region	Multiline Basic String
	public static final Pattern MLB_QUOTES = repeats(QUOTATION_MARK, 1, 2);
	public static final Pattern MLB_UNESCAPED = or(
			WSCHAR,
			Pattern.compile("\\x21\\x23-\\x5B\\x5D-\\x7E"),
			NON_ASCII
	);
	public static final Pattern MLB_ESCAPED_NL = chain(
			ESCAPE,
			WS,
			NEWLINE,
			repeats(or(
					WSCHAR,
					NEWLINE
			))
	);
	public static final Pattern MLB_CHAR = or(MLB_UNESCAPED, ESCAPED);
	public static final Pattern MLB_CONTENT = or(
			MLB_CHAR,
			NEWLINE,
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
	public static final Pattern ML_BASIC_STRING_DELIM = repeats(QUOTATION_MARK, 3);
	public static final Pattern ML_BASIC_STRING = chain(
			ML_BASIC_STRING_DELIM,
			optional(NEWLINE),
			ML_BASIC_BODY,
			ML_BASIC_STRING_DELIM
	);
	// End



	// Region	Literal String
	public static final Pattern APOSTROPHE = Pattern.compile("'");

	public static final Pattern LITERAL_CHAR = or(
		Pattern.compile("[\\x09\\x20-\\x26\\x28-\\x7E]"),
			NON_ASCII
	);
	public static final Pattern LITERAL_STRING = chain(
			APOSTROPHE,
			repeats(LITERAL_CHAR),
			APOSTROPHE
	);
	// End



	// Region	Multiline Literal String
	public static final Pattern MLL_QUOTES = repeats(APOSTROPHE, 1, 2);
	public static final Pattern MLL_CHAR = or(
			Pattern.compile("[\\x09\\x20-\\x26\\x28-\\x7E]"),
			NON_ASCII
	);
	public static final Pattern MLL_CONTENT = or(
			MLL_CHAR,
			NEWLINE
	);

	public static final Pattern ML_LITERAL_BODY = chain(
			repeats(MLL_CONTENT),
			repeats(chain(
					MLL_QUOTES,
					repeatsAtLeast(MLL_CONTENT, 1)
			)),
			optional(MLL_QUOTES)
	);
	public static final Pattern ML_LITERAL_STRING_DELIM = repeats(APOSTROPHE, 3);
	public static final Pattern ML_LITERAL_STRING = chain(
			ML_LITERAL_STRING_DELIM,
			optional(NEWLINE),
			ML_LITERAL_BODY,
			ML_LITERAL_STRING_DELIM
	);
	// End



	// Region	Integer
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
			DEC_INT,
			HEX_INT,
			OCT_INT,
			BIN_INT
	);
	// End



	// Region	Float
	public static final Pattern INF = Pattern.compile("inf");
	public static final Pattern NAN = Pattern.compile("nan");
	public static final Pattern SPECIAL_FLOAT = chain(
			optional(or(
					MINUS,
					PLUS
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
							UNDERSCORE,
							DIGIT
					)
			))
	);
	public static final Pattern DECIMAL_POINT = Pattern.compile("\\.");
	public static final Pattern FRAC = chain(
			DECIMAL_POINT,
			ZERO_PREFIXABLE_INT
	);
	public static final Pattern FLOAT_INT_PART = DEC_INT;

	public static final Pattern FLOAT_EXP_PART = chain(
			optional(or(
					MINUS,
					PLUS
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
	// End



	// Region	Boolean
	public static final Pattern TRUE = Pattern.compile("true");
	public static final Pattern FALSE = Pattern.compile("false");

	public static final Pattern BOOLEAN = or(
			TRUE,
			FALSE
	);
	// End



	// Region	Date and Time (as defined in RFC 3339)
	public static final Pattern DATE_FULL_YEAR = repeats(DIGIT, 4);
	public static final Pattern DATE_MONTH = repeats(DIGIT, 2);
	public static final Pattern DATE_MDAY = repeats(DIGIT, 2);
	public static final Pattern TIME_DELIM = or(
			c('T'),
			Pattern.compile(" ")
	);
	public static final Pattern TIME_HOUR = repeats(DIGIT, 2);
	public static final Pattern TIME_MINUTE = repeats(DIGIT, 2);
	public static final Pattern TIME_SECOND = repeats(DIGIT, 2);
	public static final Pattern TIME_SECFRAC = chain(
			Pattern.compile("\\."),
			repeatsAtLeast(DIGIT, 1)
	);
	public static final Pattern TIME_NUMOFFSET = chain(
			or(
					Pattern.compile("\\+"),
					Pattern.compile("-")
			),
			TIME_HOUR,
			Pattern.compile(":"),
			TIME_MINUTE
	);
	public static final Pattern TIME_OFFSET = or(
			c('Z'),
			TIME_NUMOFFSET
	);

	public static final Pattern PARTIAL_TIME = chain(
			TIME_HOUR,
			Pattern.compile(":"),
			TIME_MINUTE,
			Pattern.compile(":"),
			TIME_SECOND,
			optional(TIME_SECFRAC)
	);
	public static final Pattern FULL_DATE = chain(
			DATE_FULL_YEAR,
			Pattern.compile("-"),
			DATE_MONTH,
			Pattern.compile("-"),
			DATE_MDAY
	);
	public static final Pattern FULL_TIME = chain(
			PARTIAL_TIME,
			TIME_OFFSET
	);

	public static final Pattern OFFSET_DATE_TIME = chain(
			FULL_DATE,
			TIME_DELIM,
			FULL_TIME
	);
	public static final Pattern LOCAL_DATE_TIME = chain(
			FULL_DATE,
			TIME_DELIM,
			PARTIAL_TIME
	);
	public static final Pattern LOCAL_DATE = FULL_DATE;
	public static final Pattern LOCAL_TIME = PARTIAL_TIME;

	public static final Pattern DATE_TIME = or(
			LOCAL_DATE_TIME,
			OFFSET_DATE_TIME,
			LOCAL_DATE,
			LOCAL_TIME
	);
	// End



	// Region	Key
	public static final Pattern DOT_SEP = chain(repeats(WS), Pattern.compile("\\."), repeats(WS));
	public static final Pattern KEYVAL_SEP = chain(repeats(WS), Pattern.compile("="), repeats(WS));

	public static final Pattern UNQUOTED_KEY = repeatsAtLeast(or(
			ALPHA,
			DIGIT,
			Pattern.compile("-"),
			Pattern.compile("_")
	), 1);
	public static final Pattern QUOTED_KEY = or(
			BASIC_STRING,
			LITERAL_STRING
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
	// End



	// Region	Array
	public static final Pattern ARRAY_OPEN = Pattern.compile("\\[");
	public static final Pattern ARRAY_CLOSE = Pattern.compile("]");
	public static final Pattern ARRAY_SEP = Pattern.compile(",");

	public static final Pattern WS_COMMENT_NEWLINE = repeats(or(
			WSCHAR,
			chain(
					optional(COMMENT),
					NEWLINE
			)
	));
	// End



	// Region	Table
	public static final Pattern STD_TABLE_OPEN = chain(
			Pattern.compile("\\["),
			WS
	);
	public static final Pattern STD_TABLE_CLOSE = chain(
			WS,
			Pattern.compile("]")
	);

	public static final Pattern STD_TABLE = chain(
			STD_TABLE_OPEN,
			KEY,
			STD_TABLE_CLOSE
	);

	public static final Pattern ARRAY_TABLE_OPEN = chain(
			Pattern.compile("\\[\\["),
			WS
	);
	public static final Pattern ARRAY_TABLE_CLOSE = chain(
			WS,
			Pattern.compile("]]")
	);

	public static final Pattern ARRAY_TABLE = chain(
			ARRAY_TABLE_OPEN,
			KEY,
			ARRAY_TABLE_CLOSE
	);

	public static final Pattern TABLE = or(
			STD_TABLE,
			ARRAY_TABLE
	);
	// End



	// Region	Value
	public static final Pattern STRING = or(
			ML_BASIC_STRING,
			BASIC_STRING,
			ML_LITERAL_STRING,
			LITERAL_STRING
	);

	public static final Pattern VAL = or(
			STRING,
			BOOLEAN,
			DATE_TIME,
			FLOAT,
			INTEGER
	);
	// End



	// Region	Key-Value Pair
	public static final Pattern KEYVAL = chain(
			KEY,
			KEYVAL_SEP,
			VAL
	);
	// End



	public static final Pattern EXPRESSION = or(
			chain(
					WS,
					optional(COMMENT)
			),
			chain(
					WS,
					KEYVAL,
					WS,
					optional(COMMENT)
			),
			chain(
					WS,
					TABLE,
					WS,
					optional(COMMENT)
			)
	);

	public static final Pattern TOML = chain(
			EXPRESSION,
			repeats(chain(
					NEWLINE,
					EXPRESSION
			))
	);
}
