package MathPlot.util;

public final class InputNormalizer {

    private InputNormalizer() {}

    public static String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }
}
