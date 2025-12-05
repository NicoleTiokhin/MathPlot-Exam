package MathPlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import MathPlot.Parsers.AOS;
import MathPlot.Parsers.RPN;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class MathPlot {

    // ============================================================
    // ORIGINAL TEACHER PLOTTER — UNCHANGED (Option T)
    // ============================================================
    private class Plotter implements PlotterInterface {
        private interface PlotterItem {
            void plot();
        }

        private abstract class PlotterBase implements PlotterItem {
            final protected GraphicsContext gc;
            final protected Color color;
            final protected double lineWidth;

            public PlotterBase(Color color, double lineWidth) {
                this.gc = Plotter.this.canvas.getGraphicsContext2D();
                this.color = color;
                this.lineWidth = lineWidth;
            }
        }

        final List<PlotterItem> items;
        final private Canvas canvas;
        private Point min, max;
        private Point lastMouse;

        private class Circle extends PlotterBase {
            final private Point c;
            final private double r;

            public Circle(Point c, double r, Color color, double lineWidth) {
                super(color, lineWidth);
                this.c = c;
                this.r = r;
            }

            @Override
            public void plot() {
                this.gc.setStroke(this.color);
                this.gc.setLineWidth(this.lineWidth);
                this.gc.strokeOval(-this.r + this.c.x(), -this.r + this.c.y(),
                                   2 * this.r + this.c.x(), 2 * this.r + this.c.y());
            }
        }

        private class Curve extends PlotterBase {
            final private Point.Iterator ptIt;

            public Curve(Point.Iterator ptIt, Color color, double lineWidth) {
                super(color, lineWidth);
                this.ptIt = ptIt;
            }

            @Override
            public void plot() {
                this.ptIt.reset();
                if (!this.ptIt.hasNext()) {
                    return;
                }

                this.gc.setLineWidth(this.lineWidth);
                this.gc.setStroke(this.color);

                this.gc.beginPath();
                Point origin = this.ptIt.nextPoint();
                this.gc.moveTo(origin.x(), origin.y());

                while (this.ptIt.hasNext()) {
                    final Point np = this.ptIt.nextPoint();
                    if (!this.ptIt.hasBreak()) {
                        this.gc.lineTo(np.x(), np.y());
                    }
                    this.gc.moveTo(np.x(), np.y());
                }
                this.gc.stroke();
            }
        }

        private class Line extends PlotterBase {
            final private Point from;
            final private Point to;

            public Line(Point from, Point to, Color color, double lineWidth) {
                super(color, lineWidth);
                this.from = from;
                this.to = to;
            }

            @Override
            public void plot() {
                this.gc.setStroke(this.color);
                this.gc.setLineWidth(this.lineWidth);
                this.gc.strokeLine(this.from.x(), this.from.y(),
                                   this.to.x(), this.to.y());
            }
        }

        public Plotter(Canvas canvas, Point min, Point max) {
            this.min = min;
            this.max = max;
            this.items = new ArrayList<>();
            this.canvas = canvas;

            this.canvas.widthProperty().addListener(e -> render());
            this.canvas.heightProperty().addListener(e -> render());


            this.canvas.setOnMousePressed(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.lastMouse = new Point(e.getX(), e.getY());
                }
            });

            this.canvas.setOnMouseDragged(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    double dx = e.getX() - this.lastMouse.x();
                    double dy = e.getY() - this.lastMouse.y();

                    double width = this.canvas.getWidth();
                    double height = this.canvas.getHeight();

                    double dxUnits = (dx / width) * (this.max.x() - this.min.x());
                    double dyUnits = (dy / height) * (this.max.y() - this.min.y());

                    this.min = new Point(this.min.x() - dxUnits,
                                         this.min.y() + dyUnits);
                    this.max = new Point(this.max.x() - dxUnits,
                                         this.max.y() + dyUnits);
                    this.lastMouse = new Point(e.getX(), e.getY());

                    render();
                }
            });

            this.canvas.addEventHandler(ScrollEvent.SCROLL, e -> {
                double zoomFactor = (e.getDeltaY() > 0) ? 0.9 : 1.1;

                double mouseX = e.getX();
                double mouseY = e.getY();
                double width = this.canvas.getWidth();
                double height = this.canvas.getHeight();

                double mouseXUnit = this.min.x() +
                    (mouseX / width) * (this.max.x() - this.min.x());
                double mouseYUnit = this.max.y() -
                    (mouseY / height) * (this.max.y() - this.min.y());

                double newWidth = (this.max.x() - this.min.x()) * zoomFactor;
                double newHeight = (this.max.y() - this.min.y()) * zoomFactor;

                double xMin = mouseXUnit - (mouseX / width) * newWidth;
                double xMax = xMin + newWidth;
                double yMax = mouseYUnit + (mouseY / height) * newHeight;
                double yMin = yMax - newHeight;

                this.min = new Point(xMin, yMin);
                this.max = new Point(xMax, yMax);

                render();
            });
        }

        @Override public void addCircle(Point c, double r, Color color, double lineWidth) {
            this.items.add(new Circle(c, r, color, lineWidth));
        }

        @Override public void addLine(Point from, Point to, Color color, double lineWidth) {
            this.items.add(new Line(from, to, color, lineWidth));
        }

        @Override public void addCurve(Point.Iterator ptIt, Color color, double lineWidth) {
            this.items.add(new Curve(ptIt, color, lineWidth));
        }

        @Override public Canvas getCanvas() {
            return this.canvas;
        }

        public void render() {
            double width = this.canvas.getWidth();
            double height = this.canvas.getHeight();

            final GraphicsContext gc = this.canvas.getGraphicsContext2D();

            gc.setTransform(new Affine());
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, width, height);

            Affine transform = new Affine();
            transform.appendTranslation(0, height);
            transform.appendScale(1, -1);
            transform.appendScale(width / (this.max.x() - this.min.x()),
                                  height / (this.max.y() - this.min.y()));
            transform.appendTranslation(-this.min.x(), -this.min.y());

            gc.setTransform(transform);

            for (final PlotterItem item : this.items) {
                item.plot();
            }
        }
    }

    // ============================================================
    // PUBLIC ENUMS
    // ============================================================
    public enum PlotType { Cartesian, Polar }
    public enum ExpressionFormat { AOS, RPN }
    public enum AreaType { Rectangular, Trapezoidal }

    // ============================================================
    // INTERNAL STATE
    // ============================================================
    private Expr expr;
    private Expr deriv;

    // ============================================================
    // CONSTRUCTOR — YOU CAN CHANGE HERE
    // ============================================================
    public MathPlot() {
        // YOU CAN CHANGE HERE
        this.expr = null;
        this.deriv = null;
    }

    // ============================================================
    // SET EXPRESSION — YOU CAN CHANGE HERE
    // ============================================================
    public void setExpression(String exprStr, ExpressionFormat format) {

    // ALWAYS normalize input first
    String cleaned = normalizeInput(exprStr);

    try {

        if (format == ExpressionFormat.RPN) {

            // ln must be mapped to log for the RPN parser
            cleaned = cleaned.replaceAll("\\bln\\b", "log");

            // Split into tokens, remove blank tokens
            String[] raw = cleaned.split("\\s+");
            StringBuilder sb = new StringBuilder();

            for (String t : raw) {
                if (!t.isBlank()) {
                    sb.append(t).append(" ");
                }
            }

            String fixed = sb.toString().trim();

            // Now parse the cleaned RPN
            RPN rpn = new RPN(fixed);
            Stack<String> tokens = rpn.parse();

            this.expr = parseFromRpnTokens(tokens);

        } else {  
            // AOS branch — unchanged
            this.expr = parseFromAos(cleaned);
        }

    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid expression: " + exprStr, e);
    }

    // MUST NOT BE INSIDE TRY (exam requirement)
    this.deriv = ExprSimplifier.simplify(this.expr.diff());
}


    // ============================================================
    // PLOTTING — YOU CAN CHANGE HERE
    // ============================================================
    public void plot(Canvas canvas, PlotType type) {
        final Plotter pf = new Plotter(
                canvas, new Point(-10, -10), new Point(10, 10)
        );

        // YOU CAN CHANGE HERE
        if (expr == null) {
            pf.render();
            return;
        }

        // Axes
        pf.addLine(new Point(-10, 0), new Point(10, 0),
                   Color.LIGHTGRAY, 0.5);
        pf.addLine(new Point(0, -10), new Point(0, 10),
                   Color.LIGHTGRAY, 0.5);

        if (type == PlotType.Cartesian) {

            pf.addCurve(new SampleIterator(expr, -10, 10, 0.05),
                        Color.BLUE, 1.5);

            pf.addCurve(new SampleIterator(deriv, -10, 10, 0.05),
                        Color.RED, 1.0);

        } else { // POLAR

            for (double r = 2; r <= 10; r += 2)
                pf.addCircle(new Point(0, 0), r,
                             Color.LIGHTGRAY, 0.5);

            for (int angle = 0; angle < 360; angle += 45) {
                double a = Math.toRadians(angle);
                pf.addLine(new Point(0, 0),
                           new Point(10 * Math.cos(a),
                                     10 * Math.sin(a)),
                           Color.LIGHTGRAY, 0.5);
            }

            Point.Iterator polarF = new SampleIterator(expr,
                    0, Math.PI * 2, 0.01) {
                @Override public Point nextPoint() {
                    double r = f.eval(x);
                    Point p = new Point(
                            r * Math.cos(x),
                            r * Math.sin(x)
                    );
                    x += step;
                    return p;
                }
            };

            // **FIX REQUIRED BY EXAM: plot derivative in polar**
            Point.Iterator polarD = new SampleIterator(deriv,
                    0, Math.PI * 2, 0.01) {
                @Override public Point nextPoint() {
                    double r = deriv.eval(x);
                    Point p = new Point(
                            r * Math.cos(x),
                            r * Math.sin(x)
                    );
                    x += step;
                    return p;
                }
            };

            pf.addCurve(polarF, Color.BLUE, 1.5);
            pf.addCurve(polarD, Color.RED, 1.0);
        }

        pf.render();
    }

    // ============================================================
    // AREA — YOU CAN CHANGE HERE
    // ============================================================
    public double area(AreaType type) {
        // YOU CAN CHANGE HERE
        return area(-10, 10, type);
    }

    // ============================================================
    // PRINT — YOU CAN CHANGE HERE
    // ============================================================
    public List<String> print(ExpressionFormat format) {
        final List<String> out = new ArrayList<>();

        // YOU CAN CHANGE HERE
        if (expr == null) return out;

        Expr eS = ExprSimplifier.simplify(expr);
        Expr dS = ExprSimplifier.simplify(deriv);

        if (format == ExpressionFormat.RPN) {
            out.add("f = " + eS.toRPN());
            out.add("f' = " + dS.toRPN());
        } else {
            out.add("f = " + cleanAOS(eS.toAOS()));
            out.add("f' = " + cleanAOS(dS.toAOS()));
        }

        return out;
    }

    private String cleanAOS(String aos) {
        return aos.replaceAll("^\\((.*)\\)$", "$1");
    }

    // ============================================================
    // INTERNAL EXPRESSION SYSTEM (UNCHANGED FROM YOUR VERSION)
    // ============================================================

    private interface Expr {
        double eval(double x);
        Expr diff();
        String toAOS();
        String toRPN();
    }

    private static final class Const implements Expr {
        final double value;
        Const(double v) { this.value = v; }

        public double eval(double x) { return value; }
        public Expr diff() { return new Const(0); }
        public String toAOS() { return Double.toString(value); }
        public String toRPN() { return Double.toString(value); }
    }

    private static final class Var implements Expr {
        public double eval(double x) { return x; }
        public Expr diff() { return new Const(1); }
        public String toAOS() { return "x"; }
        public String toRPN() { return "x"; }
    }

    private static final class Unary implements Expr {
        enum Fun { SIN, COS, EXP, LN }
        final Fun fun;
        final Expr arg;

        Unary(Fun f, Expr arg) {
            this.fun = f;
            this.arg = arg;
        }

        public double eval(double x) {
            double v = arg.eval(x);
            return switch (fun) {
                case SIN -> Math.sin(v);
                case COS -> Math.cos(v);
                case EXP -> Math.exp(v);
                case LN  -> Math.log(v);
            };
        }

        public Expr diff() {
            Expr d = arg.diff();
            return switch (fun) {
                case SIN -> new Binary(Binary.Op.MUL,
                                       new Unary(Fun.COS, arg), d);

                case COS -> new Binary(Binary.Op.MUL,
                                       new Const(-1),
                                       new Binary(Binary.Op.MUL,
                                                  new Unary(Fun.SIN, arg), d));

                case EXP -> new Binary(Binary.Op.MUL,
                                       new Unary(Fun.EXP, arg), d);

                case LN -> new Binary(Binary.Op.MUL,
                                      new Binary(Binary.Op.DIV,
                                                 new Const(1), arg),
                                      d);
            };
        }

        public String toAOS() {
            String f = switch(fun) {
                case SIN -> "sin";
                case COS -> "cos";
                case EXP -> "exp";
                case LN  -> "ln";
            };
            return f + "(" + arg.toAOS() + ")";
        }

        public String toRPN() {
            String f = switch(fun) {
                case SIN -> "sin";
                case COS -> "cos";
                case EXP -> "exp";
                case LN  -> "log";
            };
            return arg.toRPN() + " " + f;
        }
    }

    private static final class Binary implements Expr {
        enum Op { ADD, SUB, MUL, DIV, POW }
        final Op op;
        final Expr a, b;

        Binary(Op op, Expr a, Expr b) {
            this.op = op;
            this.a = a;
            this.b = b;
        }

        public double eval(double x) {
            double L = a.eval(x), R = b.eval(x);
            return switch (op) {
                case ADD -> L + R;
                case SUB -> L - R;
                case MUL -> L * R;
                case DIV -> L / R;
                case POW -> Math.pow(L, R);
            };
        }

        public Expr diff() {
    return switch (op) {

        case ADD -> new Binary(Op.ADD, a.diff(), b.diff());

        case SUB -> new Binary(Op.SUB, a.diff(), b.diff());

        case MUL -> new Binary(Op.ADD,
                new Binary(Op.MUL, a.diff(), b),
                new Binary(Op.MUL, a, b.diff()));

        case DIV -> new Binary(Op.DIV,
                new Binary(Op.SUB,
                        new Binary(Op.MUL, a.diff(), b),
                        new Binary(Op.MUL, a, b.diff())),
                new Binary(Op.POW, b, new Const(2)));

        case POW -> {
            if (b instanceof Const c) {
                yield new Binary(
                        Op.MUL,
                        new Const(c.value),
                        new Binary(
                                Op.MUL,
                                new Binary(Op.POW, a, new Const(c.value - 1)),
                                a.diff()
                        )
                );
            }
                            throw new UnsupportedOperationException("Non-constant exponent not supported");
            }
        };
    }


                
        public String toAOS() {
            return "(" + a.toAOS() + " " +
                switch(op) {
                    case ADD -> "+";
                    case SUB -> "-";
                    case MUL -> "*";
                    case DIV -> "/";
                    case POW -> "^";
                } + " " + b.toAOS() + ")";
        }

        public String toRPN() {
            return a.toRPN() + " " + b.toRPN() + " " +
                switch(op) {
                    case ADD -> "+";
                    case SUB -> "-";
                    case MUL -> "*";
                    case DIV -> "/";
                    case POW -> "^";
                };
        }
    }

    // ============================================================
    // Simplifier — unchanged
    // ============================================================
    private static class ExprSimplifier {
        static Expr simplify(Expr e) {
            if (e instanceof Const || e instanceof Var)
                return e;

            if (e instanceof Unary u)
                return new Unary(u.fun, simplify(u.arg));

            if (e instanceof Binary b) {
                Expr A = simplify(b.a),
                     B = simplify(b.b);

                if (b.op == Binary.Op.ADD) {
                    if (A instanceof Const c && c.value == 0) return B;
                    if (B instanceof Const c && c.value == 0) return A;
                }

                if (b.op == Binary.Op.SUB) {
                    if (B instanceof Const c && c.value == 0) return A;
                }

                if (b.op == Binary.Op.MUL) {
                    if (A instanceof Const c) {
                        if (c.value == 0) return new Const(0);
                        if (c.value == 1) return B;
                    }
                    if (B instanceof Const c) {
                        if (c.value == 0) return new Const(0);
                        if (c.value == 1) return A;
                    }
                }

                if (b.op == Binary.Op.POW) {
                    if (B instanceof Const c) {
                        if (c.value == 1) return A;
                        if (c.value == 0) return new Const(1);
                    }
                }

                return new Binary(b.op, A, B);
            }

            return e;
        }
    }

    // ============================================================
    // Parsing (RPN + AOS)
    // ============================================================

    private Expr parseFromRpnTokens(Stack<String> tokens) throws Exception {
        if (tokens.isEmpty())
            throw new IllegalArgumentException("Empty RPN");

        String token = tokens.pop();

        try {
            return new Const(Double.parseDouble(token));
        }
        catch (Exception ignored) {}

        if (token.equals("x"))
            return new Var();

        String t = token.toLowerCase();

        if (t.equals("sin") || t.equals("cos") ||
            t.equals("exp") || t.equals("log") ||
            t.equals("ln")) {

            Expr arg = parseFromRpnTokens(tokens);
            Unary.Fun fun = switch (t) {
                case "sin" -> Unary.Fun.SIN;
                case "cos" -> Unary.Fun.COS;
                case "exp" -> Unary.Fun.EXP;
                case "log", "ln" -> Unary.Fun.LN;
                default -> throw new IllegalArgumentException("Unknown function");
            };

            return new Unary(fun, arg);
        }

        if ("+-*/^".contains(token)) {
            Expr right = parseFromRpnTokens(tokens);
            Expr left = parseFromRpnTokens(tokens);

            return switch (token) {
                case "+" -> new Binary(Binary.Op.ADD, left, right);
                case "-" -> new Binary(Binary.Op.SUB, left, right);
                case "*" -> new Binary(Binary.Op.MUL, left, right);
                case "/" -> new Binary(Binary.Op.DIV, left, right);
                case "^" -> new Binary(Binary.Op.POW, left, right);
                default -> throw new IllegalArgumentException("Unknown operator");
            };
        }

        throw new IllegalArgumentException("Bad RPN token: " + token);
    }

    private Expr parseFromAos(String exprStr) throws Exception {
        return parseFromAosRecursive(exprStr, new AOS());
    }

    private Expr parseFromAosRecursive(String input, AOS aos) throws Exception {
        AOS.Parts p = aos.parse(input.trim());
        String main = p.main.trim();

        if (p.left == null && p.right == null) {
            if (main.equals("x"))
                return new Var();

            try {
                return new Const(Double.parseDouble(main));
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Unknown atom: " + main);
            }
        }

        if (p.right == null) {
            Expr arg = parseFromAosRecursive(p.left, aos);

            Unary.Fun fun = switch (main.toLowerCase()) {
                case "sin" -> Unary.Fun.SIN;
                case "cos" -> Unary.Fun.COS;
                case "exp" -> Unary.Fun.EXP;
                case "ln", "log" -> Unary.Fun.LN;
                default -> throw new IllegalArgumentException("Unknown function: " + main);
            };

            return new Unary(fun, arg);
        }

        Expr L = parseFromAosRecursive(p.left, aos);
        Expr R = parseFromAosRecursive(p.right, aos);

        return switch (main) {
            case "+" -> new Binary(Binary.Op.ADD, L, R);
            case "-" -> new Binary(Binary.Op.SUB, L, R);
            case "*" -> new Binary(Binary.Op.MUL, L, R);
            case "/" -> new Binary(Binary.Op.DIV, L, R);
            case "^" -> new Binary(Binary.Op.POW, L, R);
            default -> throw new IllegalArgumentException("Unknown operator: " + main);
        };
    }

    // ============================================================
    // Sample Iterator — unchanged
    // ============================================================
    private static class SampleIterator implements Point.Iterator {
        protected final Expr f;
        protected final double from;
        protected final double to;
        protected final double step;
        protected double x;

        SampleIterator(Expr f, double from, double to, double step) {
            this.f = f;
            this.from = from;
            this.to = to;
            this.step = step;
            this.x = from;
        }

        public void reset() { x = from; }
        public boolean hasNext() { return x <= to; }

        public Point nextPoint() {
            double y = f.eval(x);
            Point p = new Point(x, y);
            x += step;
            return p;
        }

        public boolean hasBreak() { return false; }
    }

    // ============================================================
    // EXTRA PUBLIC API — inside NEW legal "YOU CAN CHANGE HERE"
    // ============================================================
    // YOU CAN CHANGE HERE — ADDITIONAL PUBLIC METHODS ALLOWED

    /** Evaluate f(x). */
    public double evaluate(double x) {
        if (expr == null)
            throw new IllegalStateException("Expression not set");
        return expr.eval(x);
    }

    /** Evaluate f'(x). */
    public double evaluateDerivative(double x) {
        if (deriv == null)
            throw new IllegalStateException("Derivative not set");
        return deriv.eval(x);
    }

    /** Public area(from,to,type) */
    public double area(double from, double to, AreaType type) {
        double step = 0.01;
        double acc = 0.0;

        for (double x = from; x < to; x += step) {
            double y1 = expr.eval(x);
            double y2 = expr.eval(x + step);

            if (type == AreaType.Rectangular)
                acc += y1 * step;
            else
                acc += 0.5 * (y1 + y2) * step;
        }

        return acc;
    }
    // YOU CAN CHANGE HERE
    private String normalizeInput(String s) {
        if (s == null) return "";
        // Remove leading/trailing whitespace, collapse internal whitespace
        return s.trim().replaceAll("\\s+", " ");
    }

}
