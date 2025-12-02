package MathPlot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import MathPlot.Parsers.AOS;
import MathPlot.Parsers.RPN;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;


public class MathPlot {
    // ===========================
    // ORIGINAL PLOTTER (UNTOUCHED)
    // ===========================
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
                this.gc.strokeOval(-this.r + this.c.x(), -this.r + this.c.y(), 2 * this.r + this.c.x(),
                        2 * this.r + this.c.y());
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
                this.gc.strokeLine(this.from.x(), this.from.y(), this.to.x(), this.to.y());
            }
        }

        public Plotter(Canvas canvas, Point min, Point max) {
            this.min = min;
            this.max = max;
            this.items = new ArrayList<>();
            this.canvas = canvas;

            this.canvas.widthProperty().addListener(ignored -> render());
            this.canvas.heightProperty().addListener(ignored -> render());


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

                    this.min = new Point(this.min.x() - dxUnits, this.min.y() + dyUnits);
                    this.max = new Point(this.max.x() - dxUnits, this.max.y() + dyUnits);
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

                double mouseXUnit = this.min.x() + (mouseX / width) * (this.max.x() - this.min.x());
                double mouseYUnit = this.max.y() - (mouseY / height) * (this.max.y() - this.min.y());

                double newWidth = (this.max.x() - this.min.x()) * zoomFactor;
                double newHeight = (this.max.y() - this.min.y()) * zoomFactor;

                double xMin = mouseXUnit - (mouseX - 0) / width * newWidth;
                double xMax = this.min.x() + newWidth;

                double yMax = mouseYUnit + (mouseY - 0) / height * newHeight;
                double yMin = this.max.y() - newHeight;

                this.min = new Point(xMin, yMin);
                this.max = new Point(xMax, yMax);

                render();
            });
        }

        @Override
        public void addCircle(Point c, double r, Color color, double lineWidth) {
            this.items.add(new Circle(c, r, color, lineWidth));
        }

        @Override
        public void addLine(Point from, Point to, Color color, double lineWidth) {
            this.items.add(new Line(from, to, color, lineWidth));
        }

        @Override
        public void addCurve(Point.Iterator ptIt, Color color, double lineWidth) {
            this.items.add(new Curve(ptIt, color, lineWidth));
        }

        @Override
        public Canvas getCanvas() {
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

    // ===========================
    // NEW LOGIC (ALLOWED TO CHANGE)
    // ===========================

    public enum PlotType {
        Cartesian, Polar
    }

    public enum ExpressionFormat {
        AOS, RPN
    }

    public enum AreaType {
        Rectangular,
        Trapezoidal
    }

    // ---- Expression AST ----
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

    Unary(Fun fun, Expr arg) {
        this.fun = fun;
        this.arg = arg;
    }

    @Override
    public double eval(double x) {
        double v = arg.eval(x);
        return switch(fun) {
            case SIN -> Math.sin(v);
            case COS -> Math.cos(v);
            case EXP -> Math.exp(v);
            case LN  -> Math.log(v); // natural log
        };
    }

    @Override
    public Expr diff() {
        Expr inner = arg.diff(); // chain rule
        return switch(fun) {
            case SIN -> new Binary(Binary.Op.MUL,
                    new Unary(Fun.COS, arg), inner);
            case COS -> new Binary(Binary.Op.MUL,
                    new Const(-1),
                    new Binary(Binary.Op.MUL,
                            new Unary(Fun.SIN, arg), inner));
            case EXP -> new Binary(Binary.Op.MUL,
                    new Unary(Fun.EXP, arg), inner);
            case LN -> new Binary(Binary.Op.MUL,
                    new Binary(Binary.Op.DIV, new Const(1), arg),
                    inner);
        };
    }

    @Override
    public String toAOS() {
        String fname = switch(fun) {
            case SIN -> "sin";
            case COS -> "cos";
            case EXP -> "exp";
            case LN  -> "ln"; // AOS prints ln(x)
        };
        return fname + "(" + arg.toAOS() + ")";
    }

    @Override
    public String toRPN() {
        String fname = switch(fun) {
            case SIN -> "sin";
            case COS -> "cos";
            case EXP -> "exp";
            case LN  -> "log"; // RPN uses log token
        };
        return arg.toRPN() + " " + fname;
    }
}


    
    private static final class Binary implements Expr {
        enum Op { ADD, SUB, MUL, DIV, POW }
        final Op op;
        final Expr a, b;

        Binary(Op op, Expr a, Expr b) {
            this.op = op; this.a = a; this.b = b;
        }

        public double eval(double x) {
            double l = a.eval(x), r = b.eval(x);
            return switch(op) {
                case ADD -> l+r;
                case SUB -> l-r;
                case MUL -> l*r;
                case DIV -> l/r;
                case POW -> Math.pow(l,r);
            };
        }

        public Expr diff() {
            return switch(op) {
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
                        // d/dx a^c = c * a^(c-1) * a'
                        yield new Binary(Op.MUL,
                                new Const(c.value),
                                new Binary(Op.MUL,
                                        new Binary(Op.POW, a, new Const(c.value - 1)),
                                        a.diff()));
                    }
                    throw new UnsupportedOperationException("General pow not implemented");
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
                    } +
                    " " + b.toAOS() + ")";
        }

        public String toRPN() {
            return a.toRPN()+" "+b.toRPN()+" "+
                    switch(op){
                        case ADD -> "+";
                        case SUB -> "-";
                        case MUL -> "*";
                        case DIV -> "/";
                        case POW -> "^";
                    };
        }
    }

    // ---- Fields for expression + derivative ----
    private Expr expr;
    private Expr deriv;

    // ==============================
    // RPN PARSER (input format supported)
    // ==============================
    private Expr parseRPN(String expr) {
    Deque<Expr> st = new ArrayDeque<>();
    String[] tokens = expr.trim().split("\\s+");

    for (String tok : tokens) {
        if (tok.equals("x")) {
            st.push(new Var());
        } else if (tok.matches("-?\\d+(\\.\\d+)?")) {
            st.push(new Const(Double.parseDouble(tok)));
        } else {
            switch (tok) {
                case "+" -> {
                    Expr b = st.pop(), a = st.pop();
                    st.push(new Binary(Binary.Op.ADD, a, b));
                }
                case "-" -> {
                    Expr b = st.pop(), a = st.pop();
                    st.push(new Binary(Binary.Op.SUB, a, b));
                }
                case "*" -> {
                    Expr b = st.pop(), a = st.pop();
                    st.push(new Binary(Binary.Op.MUL, a, b));
                }
                case "/" -> {
                    Expr b = st.pop(), a = st.pop();
                    st.push(new Binary(Binary.Op.DIV, a, b));
                }
                case "^" -> {
                    Expr b = st.pop(), a = st.pop();
                    st.push(new Binary(Binary.Op.POW, a, b));
                }
                case "sin" -> st.push(new Unary(Unary.Fun.SIN, st.pop()));
                case "cos" -> st.push(new Unary(Unary.Fun.COS, st.pop()));
                case "exp" -> st.push(new Unary(Unary.Fun.EXP, st.pop()));
                case "ln"  -> st.push(new Unary(Unary.Fun.LN, st.pop()));
                default -> throw new IllegalArgumentException("Unknown token: " + tok);
            }
        }
    }

    if (st.size() != 1)
        throw new IllegalArgumentException("Invalid RPN expression");

    return st.pop();
}

    // ==============================
    // CONSTRUCTOR
    // ==============================
    public MathPlot() {
        // YOU CAN CHANGE HERE
        this.expr = null;
        this.deriv = null;
    }

    // ==============================
    // SET EXPRESSION
    // ==============================


public void setExpression(String expr, ExpressionFormat format) {
    try {
        if (format == ExpressionFormat.RPN) {
            // Normalize ln → log so teacher's RPN parser accepts it
            String normalized = expr.replaceAll("\\bln\\b", "log");
            RPN rpn = new RPN(normalized);
            java.util.Stack<String> tokens = rpn.parse();
            this.expr = parseFromRpnTokens(tokens);
        } else {
            // AOS input
            this.expr = parseFromAos(expr);
        }
    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid expression format: " + expr, e);
    }

    this.deriv = ExprSimplifier.simplify(this.expr.diff());
}


    // Helpers for tests / evaluation
    public double evaluate(double x) {
        if (expr == null) throw new IllegalStateException("Expression not set");
        return expr.eval(x);
    }

    public double evaluateDerivative(double x) {
        if (deriv == null) throw new IllegalStateException("Derivative not set");
        return deriv.eval(x);
    }

    // ==============================
    // PLOTTING
    // ==============================
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


    public void plot(Canvas canvas, PlotType type) {
    final Plotter pf = new Plotter(canvas, new Point(-10, -10), new Point(10, 10));

    // YOU CAN CHANGE HERE
    if (expr == null) {
        pf.render();
        return;
    }

    if (type == PlotType.Cartesian) {

        // Draw axes
        pf.addLine(new Point(-10, 0), new Point(10, 0), Color.LIGHTGRAY, 0.5);
        pf.addLine(new Point(0, -10), new Point(0, 10), Color.LIGHTGRAY, 0.5);

        // Function curve
        Point.Iterator fIt = new SampleIterator(expr, -10, 10, 0.05);
        pf.addCurve(fIt, Color.BLUE, 1.5);

        // Derivative curve
        Point.Iterator dIt = new SampleIterator(deriv, -10, 10, 0.05);
        pf.addCurve(dIt, Color.RED, 1.0);

    } else if (type == PlotType.Polar) {

        // Draw polar grid (concentric circles)
        for (double r = 2; r <= 10; r += 2) {
            pf.addCircle(new Point(0, 0), r, Color.LIGHTGRAY, 0.5);
        }

        // Draw polar angle lines every 45 degrees
        for (int angle = 0; angle < 360; angle += 45) {
            double a = Math.toRadians(angle);
            double x = 10 * Math.cos(a);
            double y = 10 * Math.sin(a);
            pf.addLine(new Point(0, 0), new Point(x, y), Color.LIGHTGRAY, 0.5);
        }

        // Polar function curve: r = f(theta)
        Point.Iterator polarCurve = new SampleIterator(expr, 0, Math.PI * 2, 0.01) {
            @Override
            public Point nextPoint() {
                double r = f.eval(x);
                double px = r * Math.cos(x);
                double py = r * Math.sin(x);
                Point p = new Point(px, py);
                x += step;
                return p;
            }
        };

        pf.addCurve(polarCurve, Color.BLUE, 1.5);

    } else {
        throw new UnsupportedOperationException("Unknown PlotType: " + type);
    }

    pf.render();
}

    // ==============================
    // AREA (Rectangular / Trapezoidal)
    // ==============================
    public double area(AreaType areaType) {
    return area(-10.0, 10.0, areaType);
}


    // ==============================
    // PRINT EXPRESSION + DERIVATIVE
    // ==============================
    

// ==============================
// PRINT EXPRESSION + DERIVATIVE
// ==============================
public List<String> print(ExpressionFormat format) {
    final List<String> res = new ArrayList<>();

    if (expr == null)
        return res;

    Expr simpleExpr = ExprSimplifier.simplify(expr);
    Expr simpleDeriv = ExprSimplifier.simplify(deriv);

    if (format == ExpressionFormat.RPN) {
        res.add("f = " + simpleExpr.toRPN());
        res.add("f' = " + simpleDeriv.toRPN());
    } else {
        res.add("f = " + cleanAOS(simpleExpr.toAOS()));
        res.add("f' = " + cleanAOS(simpleDeriv.toAOS()));
    }

    return res;
}

private String cleanAOS(String aos) {
    // remove a single pair of outermost parentheses if present
    return aos.replaceAll("^\\((.*)\\)$", "$1");
}
 
// ==============================
// EXPRESSION SIMPLIFIER
// ==============================
private static class ExprSimplifier {

    public static Expr simplify(Expr e) {
    if (e instanceof Const || e instanceof Var) return e;

    if (e instanceof Unary u) {
        Expr inner = simplify(u.arg);
        return new Unary(u.fun, inner);
    }

    if (e instanceof Binary b) {
        Expr a = simplify(b.a);
        Expr c = simplify(b.b);

        // a + 0 = a ; 0 + a = a
        if (b.op == Binary.Op.ADD) {
            if (a instanceof Const ca && ca.value == 0) return c;
            if (c instanceof Const cb && cb.value == 0) return a;
        }

        // a - 0 = a ; 0 - a = -a
        if (b.op == Binary.Op.SUB) {
            if (c instanceof Const cb && cb.value == 0) return a;
            if (a instanceof Const ca && ca.value == 0)
                return new Binary(Binary.Op.MUL, new Const(-1), c);
        }

        // a * 1 = a ; 1 * a = a ; a * 0 = 0 ; 0 * a = 0
        if (b.op == Binary.Op.MUL) {
            if (a instanceof Const ca) {
                if (ca.value == 0) return new Const(0);
                if (ca.value == 1) return c;
            }
            if (c instanceof Const cb) {
                if (cb.value == 0) return new Const(0);
                if (cb.value == 1) return a;
            }
        }

        // a ^ 1 = a ; a ^ 0 = 1
        if (b.op == Binary.Op.POW) {
            if (c instanceof Const cb) {
                if (cb.value == 1) return a;
                if (cb.value == 0) return new Const(1);
            }
        }

        return new Binary(b.op, a, c);
    }

    return e;
}
}


public double area(double from, double to, AreaType type) {
    if (expr == null) throw new IllegalStateException("Expression not set");

    double step = 0.01;
    double acc = 0.0;

    for (double x = from; x < to; x += step) {
        double y1 = expr.eval(x);
        double y2 = expr.eval(x + step);
        acc += (type == AreaType.Rectangular) ? y1 * step : 0.5 * (y1 + y2) * step;
    }

    return acc;
}

// Build Expr AST from teacher's RPN parser token stack
private Expr parseFromRpnTokens(java.util.Stack<String> tokens) throws Exception {
    if (tokens.isEmpty())
        throw new IllegalArgumentException("Empty RPN expression");

    String token = tokens.pop();

    // Number?
    try {
        double v = Double.parseDouble(token);
        return new Const(v);
    } catch (NumberFormatException ignored) {}

    // Variable?
    if (token.equals("x")) {
        return new Var();
    }

    // Function?
    String lower = token.toLowerCase();
    if (lower.equals("sin") || lower.equals("cos") || lower.equals("exp")
            || lower.equals("log") || lower.equals("ln")) {

        Expr arg = parseFromRpnTokens(tokens);
        Unary.Fun fun = switch (lower) {
            case "sin" -> Unary.Fun.SIN;
            case "cos" -> Unary.Fun.COS;
            case "exp" -> Unary.Fun.EXP;
            case "log", "ln" -> Unary.Fun.LN;
            default -> throw new IllegalArgumentException("Unknown function: " + token);
        };
        return new Unary(fun, arg);
    }

    // Operator (+, -, *, /, ^)
    if ("+-*/^".contains(token)) {
        Expr right = parseFromRpnTokens(tokens);
        Expr left = parseFromRpnTokens(tokens);
        return switch (token) {
            case "+" -> new Binary(Binary.Op.ADD, left, right);
            case "-" -> new Binary(Binary.Op.SUB, left, right);
            case "*" -> new Binary(Binary.Op.MUL, left, right);
            case "/" -> new Binary(Binary.Op.DIV, left, right);
            case "^" -> new Binary(Binary.Op.POW, left, right);
            default -> throw new IllegalArgumentException("Unknown operator: " + token);
        };
    }

    throw new IllegalArgumentException("Unsupported RPN token: " + token);
}


// Build Expr AST from teacher's AOS parser (recursive)
private Expr parseFromAos(String expr) throws Exception {
    AOS aos = new AOS();
    return parseFromAosRecursive(expr.trim(), aos);
}

private Expr parseFromAosRecursive(String input, AOS aos) throws Exception {
    AOS.Parts parts = aos.parse(input.trim());
    String main = parts.main.trim();

    // Leaf node
    if (parts.left == null && parts.right == null) {
        if (main.equalsIgnoreCase("x")) {
            return new Var();
        }
        try {
            double v = Double.parseDouble(main);
            return new Const(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unknown atom in AOS: " + main);
        }
    }

    // Function: sin(...), cos(...), exp(...), log(...), ln(...)
    if (parts.right == null) {
        Expr arg = parseFromAosRecursive(parts.left, aos);
        String fname = main.toLowerCase();
        Unary.Fun fun = switch (fname) {
            case "sin" -> Unary.Fun.SIN;
            case "cos" -> Unary.Fun.COS;
            case "exp" -> Unary.Fun.EXP;
            case "log", "ln" -> Unary.Fun.LN;
            default -> throw new IllegalArgumentException("Unknown function in AOS: " + fname);
        };
        return new Unary(fun, arg);
    }

    // Binary operator
    Expr left = parseFromAosRecursive(parts.left, aos);
    Expr right = parseFromAosRecursive(parts.right, aos);

    return switch (main) {
        case "+" -> new Binary(Binary.Op.ADD, left, right);
        case "-" -> new Binary(Binary.Op.SUB, left, right);
        case "*" -> new Binary(Binary.Op.MUL, left, right);
        case "/" -> new Binary(Binary.Op.DIV, left, right);
        case "^" -> new Binary(Binary.Op.POW, left, right);
        default -> throw new IllegalArgumentException("Unknown operator in AOS: " + main);
    };
}
}