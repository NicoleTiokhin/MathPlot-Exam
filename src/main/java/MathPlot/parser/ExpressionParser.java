package MathPlot.parser;

import MathPlot.MathPlot;
import MathPlot.ast.Expr;

public interface ExpressionParser {
    Expr parse(MathPlot ctx, String input) throws Exception;
}
