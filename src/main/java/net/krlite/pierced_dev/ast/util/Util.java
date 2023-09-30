package net.krlite.pierced_dev.ast.util;

import net.krlite.pierced_dev.ast.regex.NewLine;
import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.key.Table;
import net.krlite.pierced_dev.ast.regex.primitive.BasicString;
import net.krlite.pierced_dev.ast.regex.primitive.LiteralString;
import net.krlite.pierced_dev.ast.regex.primitive.Primitive;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;

public class Util {
    public static final String LINE_TERMINATOR = "\r\n";

    public static String hash(String string) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String normalizeStdTable(String rawStdTable) {
        return normalizeKey(rawStdTable
                .replaceAll("^" + Table.STD_TABLE_OPEN.pattern(), "")
                .replaceAll(Table.STD_TABLE_CLOSE.pattern() + "$", ""));
    }

    public static String normalizeKey(String rawKey) {
        rawKey = rawKey
                .trim()
                .replaceAll("^" + Key.DOT_SEP.pattern(), "")
                .replaceAll(Key.DOT_SEP.pattern() + "$", "");
        ArrayList<Character> letters = new ArrayList<>();

        for (char c : rawKey.toCharArray())
            letters.add(c);

        return letters.stream()
                .map(String::valueOf)
                .filter(s -> !s.trim().isEmpty())
                .reduce((s1, s2) -> s1 + s2)
                .orElse("");
    }

    public static String flatten(String normalizedKey, boolean hash) {
        Matcher dottedKeyMatcher = Key.DOTTED_KEY.matcher(normalizedKey);
        boolean dottedKeyMatched = dottedKeyMatcher.matches();

        Matcher simpleKeyMatcher = Key.SIMPLE_KEY.matcher(normalizedKey);
        boolean simpleKeyMatched = simpleKeyMatcher.matches();

        if (dottedKeyMatched) {
            Matcher innerSimpleKeyMatcher = Key.SIMPLE_KEY.matcher(normalizedKey);
            ArrayList<String> innerKeys = new ArrayList<>();

            while (innerSimpleKeyMatcher.find()) {
                String innerSimpleKey = innerSimpleKeyMatcher.group();
                innerKeys.add(flatten(innerSimpleKey, true));
            }

            return String.join(".", innerKeys);
        }

        if (simpleKeyMatched) {
            Matcher quotedKeyMatcher = Key.QUOTED_KEY.matcher(normalizedKey);
            boolean quotedKeyFound = quotedKeyMatcher.find();

            if (quotedKeyFound) {
                String quotedKey = quotedKeyMatcher.group();

                Matcher basicStringMatcher = BasicString.BASIC_STRING.matcher(quotedKey);
                boolean basicStringFound = basicStringMatcher.find();

                if (basicStringFound) {
                    String key = normalizedKey.replaceAll(BasicString.QUOTATION_MARK.pattern(), "");
                    return hash ? hash(key) : key;
                }

                Matcher literalStringMatcher = LiteralString.LITERAL_STRING.matcher(quotedKey);
                boolean literalStringFound = literalStringMatcher.find();

                if (literalStringFound) {
                    String key = normalizedKey.replaceAll(LiteralString.APOSTROPHE.pattern(), "");
                    return hash ? hash(key) : key;
                }
            }
        }

        return hash ? hash(normalizedKey) : normalizedKey;
    }

    public static String unescape(String raw) {
        Matcher escapedMatcher = BasicString.ESCAPED.matcher(raw);
        StringBuffer buffer = new StringBuffer();

        while (escapedMatcher.find()) {
            String component = escapedMatcher.group();
            component = component.replaceFirst(BasicString.ESCAPE.pattern(), "");

            if ("b".equals(component))
                escapedMatcher.appendReplacement(buffer, "\b");

            if ("f".equals(component))
                escapedMatcher.appendReplacement(buffer, "\f");

            if ("n".equals(component))
                escapedMatcher.appendReplacement(buffer, "\n");

            if ("r".equals(component))
                escapedMatcher.appendReplacement(buffer, "\r");

            if ("t".equals(component))
                escapedMatcher.appendReplacement(buffer, "\t");

            if (component.startsWith("u") || component.startsWith("U")) {
                String unicode = component.replaceFirst("[uU]", "");

                escapedMatcher.appendReplacement(buffer, "");
                buffer.appendCodePoint(Integer.parseInt(unicode, 16));
            }
        }

        escapedMatcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String escape(String raw, boolean escapeNewLines) {
        raw = raw.replaceAll(BasicString.ESCAPE.pattern() + "(?!(u([0-9a-fA-F]){4})|(U([0-9a-fA-F]){8}))", "\\\\\\\\");
        raw = raw.replaceAll("\"", "\\\\\"");
        raw = raw.replaceAll("\b", "\\\\b");
        raw = raw.replaceAll("\f", "\\\\f");

        if (escapeNewLines) {
            raw = raw.replaceAll("\n", "\\\\n");
            raw = raw.replaceAll("\r", "\\\\r");
        }

        raw = raw.replaceAll("\t", "\\\\t");

        return raw;
    }

    public static String formatLine(String rawKey, String rawValue, Class<?> clazz) {
        rawKey = formatKey(flatten(normalizeKey(rawKey), false));

        if (clazz == String.class)
            rawValue = formatStringValue(rawValue);
        if (clazz == Character.class || clazz == char.class)
            rawValue = formatCharValue(rawValue);

        return rawKey + " = " + rawValue;
    }

    private static String formatKey(String rawKey) {
        return escape(rawKey, true);
    }

    private static String formatStringValue(String rawValue) {
        Matcher newLineMatcher = NewLine.NEWLINE.matcher(rawValue);
        boolean newLineFound = newLineMatcher.find();

        if (newLineFound)
            return "\"\"\"\\" + LINE_TERMINATOR + escape(rawValue, false) + "\"\"\"";

        else return "\"" + escape(rawValue, true) + "\"";
    }

    private static String formatCharValue(String rawValue) {
        return "'" + rawValue + "'";
    }

    public static String formatStdTable(String rawStdTable) {
        return "[" + normalizeKey(rawStdTable) + "]";
    }

    public static String formatComment(String rawComment) {
        return isCommentEmpty(rawComment) ? "" : "# " + rawComment.replaceAll(NewLine.NEWLINE.pattern(), "");
    }

    public static boolean isCommentEmpty(String rawComment) {
        return rawComment.replaceAll(NewLine.NEWLINE.pattern(), "").isEmpty();
    }
}
