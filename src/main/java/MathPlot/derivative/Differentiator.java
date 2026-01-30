package MathPlot.derivative;

import MathPlot.ast.Expr;

public class Differentiator {

    public static Expr differentiate(Expr e) {
        return e.diff();
    }
}

