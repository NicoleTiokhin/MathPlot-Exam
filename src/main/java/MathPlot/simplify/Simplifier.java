package MathPlot.simplify;

import MathPlot.ast.Binary;
import MathPlot.ast.Const;
import MathPlot.ast.Expr;
import MathPlot.ast.Unary;
import MathPlot.ast.Var;

public class Simplifier {

    public static Expr simplify(Expr e) {

        if (e instanceof Const || e instanceof Var)
            return e;

        if (e instanceof Unary u) {
            Expr arg = simplify(u.arg);
            return new Unary(u.fun, arg);
        }

        if (e instanceof Binary b) {
            Expr l = simplify(b.l);
            Expr r = simplify(b.r);

            // Addition rules
            if (b.op == Binary.Op.ADD) {
                if (l instanceof Const c && c.value == 0) return r;
                if (r instanceof Const c && c.value == 0) return l;
            }

            // Multiplication rules
            if (b.op == Binary.Op.MUL) {
                if (l instanceof Const c && c.value == 0) return new Const(0);
                if (r instanceof Const c && c.value == 0) return new Const(0);
                if (l instanceof Const c && c.value == 1) return r;
                if (r instanceof Const c && c.value == 1) return l;
            }

            // Power rules
            if (b.op == Binary.Op.POW) {
                if (r instanceof Const c && c.value == 0) {
                    return new Const(1.0);
                }
            }

            return new Binary(b.op, l, r);
        }

        return e;
    }
}
