package net.krlite.pierced_dev.ast.regex.key;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Whitespace;

import java.util.regex.Pattern;

public class Table extends ABNF {
	public static final Pattern STD_TABLE_OPEN = chain(
			Pattern.compile("\\["),
			Whitespace.WS
	);
	public static final Pattern STD_TABLE_CLOSE = chain(
			Whitespace.WS,
			Pattern.compile("]")
	);

	public static final Pattern STD_TABLE = chain(
			Pattern.compile("^"),
			repeats(Whitespace.WS),
			STD_TABLE_OPEN,
			Key.KEY,
			STD_TABLE_CLOSE
	);

	public static final Pattern ARRAY_TABLE_OPEN = chain(
			Pattern.compile("\\[\\["),
			Whitespace.WS
	);
	public static final Pattern ARRAY_TABLE_CLOSE = chain(
			Whitespace.WS,
			Pattern.compile("]]")
	);

	public static final Pattern ARRAY_TABLE = chain(
			ARRAY_TABLE_OPEN,
			Key.KEY,
			ARRAY_TABLE_CLOSE
	);

	public static final Pattern TABLE = or(
			STD_TABLE,
			ARRAY_TABLE
	);
}
