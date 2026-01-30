package MathPlot.ast;

public final class Const implements Expr {
    public final double value;
    public Const(double v) { value = v; }

    @Override public double eval(double x) { return value; }
    @Override public Expr diff() { return new Const(0); }
    @Override public String toAOS() { return Double.toString(value); }
    @Override public String toRPN() { return Double.toString(value); }
}
