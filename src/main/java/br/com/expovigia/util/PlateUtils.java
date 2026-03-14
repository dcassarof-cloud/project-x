package br.com.expovigia.util;

import java.util.HashMap;
import java.util.Map;

public final class PlateUtils {

    private static final Map<Character, Character> OCR_AMBIGUITY_MAP = new HashMap<>();

    static {
        OCR_AMBIGUITY_MAP.put('O', '0');
        OCR_AMBIGUITY_MAP.put('I', '1');
        OCR_AMBIGUITY_MAP.put('Z', '2');
        OCR_AMBIGUITY_MAP.put('S', '5');
        OCR_AMBIGUITY_MAP.put('B', '8');
    }

    private PlateUtils() {
    }

    public static String normalize(String plate) {
        if (plate == null) {
            return null;
        }

        String cleaned = plate
                .toUpperCase()
                .replaceAll("[\\s-]", "")
                .replaceAll("[^A-Z0-9]", "");

        if (cleaned.isBlank()) {
            return null;
        }

        return cleaned;
    }

    public static String canonicalizeAmbiguousCharacters(String plate) {
        String normalized = normalize(plate);
        if (normalized == null) {
            return null;
        }

        StringBuilder canonical = new StringBuilder();
        for (char character : normalized.toCharArray()) {
            canonical.append(OCR_AMBIGUITY_MAP.getOrDefault(character, character));
        }
        return canonical.toString();
    }

    public static int levenshteinDistance(String source, String target) {
        if (source == null || target == null) {
            return Integer.MAX_VALUE;
        }

        int[][] dp = new int[source.length() + 1][target.length() + 1];

        for (int i = 0; i <= source.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= target.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= source.length(); i++) {
            for (int j = 1; j <= target.length(); j++) {
                int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[source.length()][target.length()];
    }
}
