package net.krlite.pierced_dev.ast.util;

import net.krlite.pierced_dev.ast.regex.key.Key;
import net.krlite.pierced_dev.ast.regex.primitive.BasicString;
import net.krlite.pierced_dev.ast.regex.primitive.LiteralString;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class NormalizeUtil {
    public static String normalizeKey(String rawKey) {
        ArrayList<Character> letters = new ArrayList<>();

        for (int i = 0; i < rawKey.length(); i++) {
            letters.add(rawKey.toCharArray()[i]);
        }

        return letters.stream()
                .map(String::valueOf)
                .filter(s -> !s.trim().isEmpty())
                .reduce((s1, s2) -> s1 + s2)
                .orElse("");
    }

    public static String unescapeKey(String normalizedKey, boolean hash) {
        Matcher dottedKeyMatcher = Key.DOTTED_KEY.matcher(normalizedKey);
        boolean dottedKeyMatched = dottedKeyMatcher.matches();

        Matcher simpleKeyMatcher = Key.SIMPLE_KEY.matcher(normalizedKey);
        boolean simpleKeyMatched = simpleKeyMatcher.matches();

        if (dottedKeyMatched) {
            Matcher innerSimpleKeyMatcher = Key.SIMPLE_KEY.matcher(normalizedKey);
            ArrayList<String> innerKeys = new ArrayList<>();

            while (innerSimpleKeyMatcher.find()) {
                String innerSimpleKey = innerSimpleKeyMatcher.group();
                innerKeys.add(unescapeKey(innerSimpleKey, true));
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
                    if (hash)
                        return hash(key);
                    else return key;
                }

                Matcher literalStringMatcher = LiteralString.LITERAL_STRING.matcher(quotedKey);
                boolean literalStringFound = literalStringMatcher.find();

                if (literalStringFound) {
                    String key = normalizedKey.replaceAll(LiteralString.APOSTROPHE.pattern(), "");
                    if (hash)
                        return hash(key);
                    else return key;
                }
            }
        }

        return normalizedKey;
    }

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
}
