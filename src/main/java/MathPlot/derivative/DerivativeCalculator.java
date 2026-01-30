package MathPlot.derivative;

import MathPlot.MathPlot;

public class DerivativeCalculator {

    private final MathPlot mp;

    public DerivativeCalculator(MathPlot mp) {
        this.mp = mp;
    }

    public double evaluate(double x) {
        return mp.evaluateDerivative(x);
    }

    // 🔥 FIX IS HERE: return the FACADE, not ast.Expr
    public MathPlot.ExprFacade getDerivativeExpr() {
        return mp.getDerivative();
    }
}
