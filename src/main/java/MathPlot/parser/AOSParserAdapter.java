package MathPlot.parser;

import MathPlot.MathPlot;
import MathPlot.Parsers.AOS;
import MathPlot.ast.Binary;
import MathPlot.ast.Const;
import MathPlot.ast.Expr;
import MathPlot.ast.Unary;
import MathPlot.ast.Var;

public class AOSParserAdapter implements ExpressionParser {

    @Override
    public Expr parse(MathPlot mp, String input) throws Exception {
        AOS.Parts p = new AOS().parse(input.trim());
        return build(p);
    }

    private Expr build(AOS.Parts p) throws Exception {

        // constant or variable
        if (p.left == null && p.right == null) {
            if (p.main.equals("x")) return new Var();
            return new Const(Double.parseDouble(p.main));
        }

        // unary function
        if (p.right == null) {
            Unary.Fun fun =
                    p.main.equalsIgnoreCase("log")
                            ? Unary.Fun.LN
                            : Unary.Fun.valueOf(p.main.toUpperCase());

            return new Unary(fun, build(new AOS().parse(p.left)));
        }

        // binary operator
        Binary.Op op = switch (p.main) {
            case "+" -> Binary.Op.ADD;
            case "-" -> Binary.Op.SUB;
            case "*" -> Binary.Op.MUL;
            case "/" -> Binary.Op.DIV;
            case "^" -> Binary.Op.POW;
            default  -> throw new IllegalArgumentException("Unknown operator: " + p.main);
        };

        return new Binary(
                op,
                build(new AOS().parse(p.left)),
                build(new AOS().parse(p.right))
        );
    }
}
