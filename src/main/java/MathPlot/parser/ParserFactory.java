package MathPlot.parser;

import java.util.EnumMap;

import MathPlot.MathPlot.ExpressionFormat;

public final class ParserFactory {

    private static final EnumMap<ExpressionFormat, ExpressionParser> PARSERS =
            new EnumMap<>(ExpressionFormat.class);

    static {
        PARSERS.put(ExpressionFormat.AOS, new AOSParserAdapter());
        PARSERS.put(ExpressionFormat.RPN, new RPNParserAdapter());
    }

    private ParserFactory() {}

    public static ExpressionParser get(ExpressionFormat fmt) {
        return PARSERS.get(fmt);
    }
}
