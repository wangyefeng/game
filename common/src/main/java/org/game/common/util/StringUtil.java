package org.game.common.util;

public class StringUtil {

    public static String decapitalize(final String name) {
        if (name.isEmpty()) {
            return name;
        }
        if (name.length() > 1 &&
                Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))) {
            return name;
        }

        final char[] chars = name.toCharArray();
        final char c = chars[0];
        final char modifiedChar = Character.toLowerCase(c);
        if (modifiedChar == c) {
            return name;
        }
        chars[0] = modifiedChar;
        return new String(chars);
    }
}
