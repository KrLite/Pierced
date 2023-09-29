package net.krlite.pierced_dev.ast.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ABNF {
	// Region	ABNF
	public static final Pattern DIGIT = Pattern.compile("[0-9]");
	public static final Pattern HEXDIG = join(DIGIT, Pattern.compile("[a-fA-F]"));
	public static final Pattern ALPHA = Pattern.compile("[a-zA-Z]");
	// End



	public static Pattern c(char c) {
		return Pattern.compile("[" + Character.toUpperCase(c) + Character.toLowerCase(c) + "]");
	}

	public static Pattern C(char c) {
		return Pattern.compile(String.valueOf(c));
	}

	public static Pattern or(Pattern... patterns) {
		StringBuilder builder = new StringBuilder();
		for (Pattern pattern : patterns) {
			builder.append(group(pattern).pattern()).append("|");
		}
		return group(Pattern.compile(builder.substring(0, builder.length() - 1)));
	}

	public static Pattern chain(Pattern... patterns) {
		StringBuilder builder = new StringBuilder();
		for (Pattern pattern : patterns) {
			builder.append(pattern.pattern());
		}
		return Pattern.compile(builder.toString());
	}

	public static Pattern join(Pattern... patterns) {
		final Pattern brackets = Pattern.compile("^\\[(?<content>.*)]$");

		StringBuilder builder = new StringBuilder();
		builder.append("[");

		for (Pattern pattern : patterns) {
			String patternString = pattern.pattern();
			Matcher matcher = brackets.matcher(patternString);

			if (matcher.matches()) {
				builder.append(matcher.group("content"));
			} else {
				builder.append(patternString);
			}
		}

		builder.append("]");
		return Pattern.compile(builder.toString());
	}

	public static Pattern repeats(Pattern pattern, int min, int max) {
		return Pattern.compile(group(pattern).pattern() + "{" + min + "," + max + "}");
	}

	public static Pattern repeats(Pattern pattern, int times) {
		return Pattern.compile(group(pattern).pattern() + "{" + times + "}");
	}

	public static Pattern repeats(Pattern pattern) {
		return Pattern.compile(group(pattern).pattern() + "*");
	}

	public static Pattern repeatsAtLeast(Pattern pattern, int min) {
		return Pattern.compile(group(pattern).pattern() + "{" + min + ",}");
	}

	public static Pattern repeatsAtMost(Pattern pattern, int max) {
		return Pattern.compile(group(pattern).pattern() + "{," + max + "}");
	}

	public static Pattern optional(Pattern pattern) {
		return Pattern.compile(group(pattern).pattern() + "?");
	}

	public static Pattern group(Pattern pattern) {
		return Pattern.compile("(" + pattern.pattern() + ")");
	}

	public static Pattern group(String name, Pattern pattern) {
		return Pattern.compile("(?<" + name + ">" + pattern.pattern() + ")");
	}
}
