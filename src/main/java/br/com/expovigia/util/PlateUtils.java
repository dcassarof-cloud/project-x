package br.com.expovigia.util;

public final class PlateUtils {

    private PlateUtils() {
    }

    public static String normalize(String plate) {
        if (plate == null) {
            return null;
        }

        return plate
                .replace(" ", "")
                .replace("-", "")
                .toUpperCase();
    }
}
