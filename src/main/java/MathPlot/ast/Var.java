package MathPlot.ast;

public final class Var implements Expr {
    @Override public double eval(double x) { return x; }
    @Override public Expr diff() { return new Const(1); }
    @Override public String toAOS() { return "x"; }
    @Override public String toRPN() { return "x"; }
}
