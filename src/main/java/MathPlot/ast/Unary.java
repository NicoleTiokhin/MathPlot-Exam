package MathPlot.ast;

public final class Unary implements Expr {

    public enum Fun { SIN, COS, EXP, LN }

    public final Fun fun;
    public final Expr arg;

    public Unary(Fun f, Expr a) {
        fun = f;
        arg = a;
    }

    @Override
    public double eval(double x) {
        double v = arg.eval(x);
        return switch (fun) {
            case SIN -> Math.sin(v);
            case COS -> Math.cos(v);
            case EXP -> Math.exp(v);
            case LN  -> Math.log(v);
        };
    }

    @Override
    public Expr diff() {
        Expr d = arg.diff();
        return switch (fun) {
            case SIN -> new Binary(Binary.Op.MUL, new Unary(Fun.COS, arg), d);
            case COS -> new Binary(Binary.Op.MUL,
                    new Const(-1),
                    new Binary(Binary.Op.MUL, new Unary(Fun.SIN, arg), d));
            case EXP -> new Binary(Binary.Op.MUL, new Unary(Fun.EXP, arg), d);
            case LN  -> new Binary(Binary.Op.MUL,
                    new Binary(Binary.Op.DIV, new Const(1), arg), d);
        };
    }

    @Override
    public String toAOS() {
        return switch (fun) {
            case SIN -> "sin(" + arg.toAOS() + ")";
            case COS -> "cos(" + arg.toAOS() + ")";
            case EXP -> "exp(" + arg.toAOS() + ")";
            case LN  -> "ln(" + arg.toAOS() + ")";
        };
    }

    @Override
    public String toRPN() {
        return arg.toRPN() + " " + switch (fun) {
            case SIN -> "sin";
            case COS -> "cos";
            case EXP -> "exp";
            case LN  -> "log";
        };
    }
}
