package MathPlot.ast;

public interface Expr {

    double eval(double x);

    Expr diff();

    String toAOS();

    String toRPN();
}
