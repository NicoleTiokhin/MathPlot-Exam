package MathPlot.expression;

import MathPlot.ast.Expr;

public class ExpressionContext {

    private Expr expr;
    private Expr deriv;

    public ExpressionContext(Expr expr, Expr deriv) {
        this.expr = expr;
        this.deriv = deriv;
    }

    public Expr getExpr() {
        return expr;
    }

    public Expr getDerivative() {
        return deriv;
    }
}
