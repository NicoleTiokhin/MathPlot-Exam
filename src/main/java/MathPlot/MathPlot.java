package MathPlot;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import MathPlot.area.AreaCalculator;
import MathPlot.ast.Expr;
import MathPlot.parser.AOSParserAdapter;
import MathPlot.parser.ExpressionParser;
import MathPlot.parser.RPNParserAdapter;
import MathPlot.plot.CartesianPlotStrategy;
import MathPlot.plot.PlotStrategy;
import MathPlot.plot.PolarPlotStrategy;
import MathPlot.simplify.Simplifier;
import MathPlot.util.ExpressionPrinter;
import MathPlot.util.InputNormalizer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class MathPlot {

    /* ===================== FACADE ===================== */
    public static class ExprFacade {
        private final Expr inner;
        public ExprFacade(Expr inner) { this.inner = inner; }
        public double eval(double x) { return inner.eval(x); }
        public ExprFacade diff() { return new ExprFacade(inner.diff()); }
        public String toAOS() { return inner.toAOS(); }
        public String toRPN() { return inner.toRPN(); }
        public Expr unwrap() { return inner; }
    }

    public enum PlotType { Cartesian, Polar }
    public enum ExpressionFormat { AOS, RPN }
    public enum AreaType { Rectangular, Trapezoidal }

    private ExprFacade expr;
    private ExprFacade deriv;

    private final EnumMap<ExpressionFormat, ExpressionParser> parsers =
            new EnumMap<>(ExpressionFormat.class);
    private final EnumMap<PlotType, PlotStrategy> plotStrategies =
            new EnumMap<>(PlotType.class);

    public MathPlot() {
        parsers.put(ExpressionFormat.AOS, new AOSParserAdapter());
        parsers.put(ExpressionFormat.RPN, new RPNParserAdapter());
        plotStrategies.put(PlotType.Cartesian, new CartesianPlotStrategy());
        plotStrategies.put(PlotType.Polar, new PolarPlotStrategy());
    }

    public void setExpression(String expression, ExpressionFormat format) {
    String normalized = InputNormalizer.normalize(expression);
    try {
        Expr parsed = parsers.get(format).parse(this, normalized);
        expr = new ExprFacade(parsed);
        deriv = expr.diff();
    } catch (UnsupportedOperationException e) {
        throw e; // REQUIRED by tests
    } catch (Exception e) {
        throw new IllegalArgumentException(e);
    }
}


    public ExprFacade getExpr() { return expr; }
    public ExprFacade getDerivative() { return deriv; }

    public double evaluate(double x) { return expr.eval(x); }
    public double evaluateDerivative(double x) { return deriv.eval(x); }

    public Point.Iterator sampleExpr(double f, double t, double s) {
        return new SampleIterator(f, t, s, false);
    }

    public Point.Iterator sampleDeriv(double f, double t, double s) {
        return new SampleIterator(f, t, s, true);
    }

    private class SampleIterator implements Point.Iterator {
        private final double to, step;
        private final boolean deriv;
        private double x;

        SampleIterator(double f, double t, double s, boolean d) {
            x = f; to = t; step = s; deriv = d;
        }

        public boolean hasNext() { return x <= to; }
        public boolean hasBreak() { return false; }

        public Point nextPoint() {
            Point p = new Point(x, deriv ? evaluateDerivative(x) : evaluate(x));
            x += step;
            return p;
        }

        public void reset() {}
    }

    public double area(double f, double t, AreaType type) {
        return new AreaCalculator(this).compute(f, t, type);
    }

    public double area(AreaType type) {
        return area(-10, 10, type);
    }

    public List<String> print(ExpressionFormat fmt) {
        List<String> out = new ArrayList<>();
        if (expr == null) return out;

        Expr se = Simplifier.simplify(expr.unwrap());
        Expr sd = Simplifier.simplify(deriv.unwrap());

        if (fmt == ExpressionFormat.RPN) {
            out.add("f = " + se.toRPN());
            out.add("f' = " + sd.toRPN());
        } else {
            out.add("f = " + ExpressionPrinter.stripOuterParens(se.toAOS()));
            out.add("f' = " + ExpressionPrinter.stripOuterParens(sd.toAOS()));
        }
        return out;
    }

    public void plot(Canvas canvas, PlotType type) {
        Plotter p = new Plotter(canvas);
        plotStrategies.get(type).plot(this, p);
        p.render();
    }

    /* ===================== PLOTTER ===================== */
    private class Plotter implements PlotterInterface {

        private interface Item { void plot(); }

        private final List<Item> items = new ArrayList<>();
        private final Canvas canvas;
        private Point min = new Point(-10, -10);
        private Point max = new Point(10, 10);
        private Point lastMouse;

        Plotter(Canvas c) {
            canvas = c;

            canvas.widthProperty().addListener((o,a,b)->render());
            canvas.heightProperty().addListener((o,a,b)->render());

            canvas.setOnMousePressed(e -> lastMouse = new Point(e.getX(), e.getY()));

            canvas.setOnMouseDragged(e -> {
                double dx = e.getX() - lastMouse.x();
                double dy = e.getY() - lastMouse.y();
                double w = canvas.getWidth();
                double h = canvas.getHeight();

                double dxU = dx / w * (max.x() - min.x());
                double dyU = dy / h * (max.y() - min.y());

                min = new Point(min.x() - dxU, min.y() + dyU);
                max = new Point(max.x() - dxU, max.y() + dyU);
                lastMouse = new Point(e.getX(), e.getY());
                render();
            });

            canvas.addEventHandler(ScrollEvent.SCROLL, e -> {
                double z = e.getDeltaY() > 0 ? 0.9 : 1.1;
                double w = canvas.getWidth(), h = canvas.getHeight();

                double nw = (max.x() - min.x()) * z;
                double nh = (max.y() - min.y()) * z;

                min = new Point(min.x(), min.y());
                max = new Point(min.x() + nw, min.y() + nh);
                render();
            });
        }

        public void addLine(Point a, Point b, Color c, double lw) {
            items.add(() -> {
                GraphicsContext g = canvas.getGraphicsContext2D();
                g.setStroke(c); g.setLineWidth(lw);
                g.strokeLine(a.x(), a.y(), b.x(), b.y());
            });
        }

        public void addCircle(Point c, double r, Color col, double lw) {
            items.add(() -> {
                GraphicsContext g = canvas.getGraphicsContext2D();
                g.setStroke(col); g.setLineWidth(lw);
                g.strokeOval(c.x() - r, c.y() - r, 2*r, 2*r);
            });
        }

        public void addCurve(Point.Iterator it, Color col, double lw) {
            items.add(() -> {
                GraphicsContext g = canvas.getGraphicsContext2D();
                g.setStroke(col); g.setLineWidth(lw);
                it.reset();
                if (!it.hasNext()) return;
                Point p = it.nextPoint();
                g.beginPath(); g.moveTo(p.x(), p.y());
                while (it.hasNext()) {
                    Point n = it.nextPoint();
                    g.lineTo(n.x(), n.y());
                }
                g.stroke();
            });
        }

        public Canvas getCanvas() { return canvas; }

        public void render() {
            GraphicsContext g = canvas.getGraphicsContext2D();
            double w = canvas.getWidth(), h = canvas.getHeight();

            g.setTransform(new Affine());
            g.setFill(Color.WHITE);
            g.fillRect(0,0,w,h);

            Affine t = new Affine();
            t.appendTranslation(0,h);
            t.appendScale(1,-1);
            t.appendScale(w/(max.x()-min.x()), h/(max.y()-min.y()));
            t.appendTranslation(-min.x(), -min.y());
            g.setTransform(t);

            for (Item i : items) i.plot();
        }
    }
}
