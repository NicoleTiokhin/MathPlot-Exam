package MathPlot.util;

public final class ExpressionPrinter {

    private ExpressionPrinter() {}

    public static String stripOuterParens(String s) {
        if (s == null || s.length() < 2) return s;
        if (s.startsWith("(") && s.endsWith(")"))
            return s.substring(1, s.length() - 1);
        return s;
    }
}
