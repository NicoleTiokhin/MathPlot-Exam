package MathPlot.ast;

public final class Binary implements Expr {

    public enum Op { ADD, SUB, MUL, DIV, POW }

    public final Op op;
    public final Expr l, r;

    public Binary(Op o, Expr a, Expr b) {
        op = o;
        l = a;
        r = b;
    }

    @Override
    public double eval(double x) {
        return switch (op) {
            case ADD -> l.eval(x) + r.eval(x);
            case SUB -> l.eval(x) - r.eval(x);
            case MUL -> l.eval(x) * r.eval(x);
            case DIV -> l.eval(x) / r.eval(x);
            case POW -> Math.pow(l.eval(x), r.eval(x));
        };
    }

    @Override
    public Expr diff() {
        return switch (op) {
            case ADD -> new Binary(Op.ADD, l.diff(), r.diff());
            case SUB -> new Binary(Op.SUB, l.diff(), r.diff());
            case MUL -> new Binary(Op.ADD,
                    new Binary(Op.MUL, l.diff(), r),
                    new Binary(Op.MUL, l, r.diff()));
            case DIV -> new Binary(Op.DIV,
                    new Binary(Op.SUB,
                            new Binary(Op.MUL, l.diff(), r),
                            new Binary(Op.MUL, l, r.diff())),
                    new Binary(Op.POW, r, new Const(2)));
            case POW -> {
                if (r instanceof Const c)
                    yield new Binary(Op.MUL,
                            new Const(c.value),
                            new Binary(Op.MUL,
                                    new Binary(Op.POW, l, new Const(c.value - 1)),
                                    l.diff()));
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String toAOS() {
        return "(" + l.toAOS() + " " + switch (op) {
            case ADD -> "+";
            case SUB -> "-";
            case MUL -> "*";
            case DIV -> "/";
            case POW -> "^";
        } + " " + r.toAOS() + ")";
    }

    @Override
    public String toRPN() {
        return l.toRPN() + " " + r.toRPN() + " " + switch (op) {
            case ADD -> "+";
            case SUB -> "-";
            case MUL -> "*";
            case DIV -> "/";
            case POW -> "^";
        };
    }
}
