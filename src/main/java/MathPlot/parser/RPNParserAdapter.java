package MathPlot.parser;

import java.util.Stack;

import MathPlot.MathPlot;
import MathPlot.Parsers.RPN;
import MathPlot.ast.Binary;
import MathPlot.ast.Const;
import MathPlot.ast.Expr;
import MathPlot.ast.Unary;
import MathPlot.ast.Var;

public class RPNParserAdapter implements ExpressionParser {

    @Override
    public Expr parse(MathPlot mp, String input) throws Exception {

        // normalize ln → log for legacy parser
        String normalized = input.replace("ln", "log");

        Stack<String> tokens = new RPN(normalized).parse();
        return build(tokens);
    }

    private Expr build(Stack<String> t) throws Exception {

        String tok = t.pop();

        // number
        try {
            return new Const(Double.parseDouble(tok));
        } catch (NumberFormatException ignored) {}

        // variable
        if (tok.equals("x")) return new Var();

        // binary
        if ("+-*/^".contains(tok)) {
            Expr b = build(t);
            Expr a = build(t);

            Binary.Op op = switch (tok) {
                case "+" -> Binary.Op.ADD;
                case "-" -> Binary.Op.SUB;
                case "*" -> Binary.Op.MUL;
                case "/" -> Binary.Op.DIV;
                case "^" -> Binary.Op.POW;
                default  -> throw new IllegalArgumentException();
            };

            return new Binary(op, a, b);
        }

        // unary
        Unary.Fun fun =
                tok.equalsIgnoreCase("log")
                        ? Unary.Fun.LN
                        : Unary.Fun.valueOf(tok.toUpperCase());

        return new Unary(fun, build(t));
    }
}
