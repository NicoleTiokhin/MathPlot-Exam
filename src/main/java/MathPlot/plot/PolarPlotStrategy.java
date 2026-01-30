package MathPlot.plot;

import MathPlot.MathPlot;
import MathPlot.PlotterInterface;
import MathPlot.Point;
import javafx.scene.paint.Color;

public class PolarPlotStrategy implements PlotStrategy {

    @Override
    public void plot(MathPlot mp, PlotterInterface p) {
        for (double r = 2; r <= 10; r += 2)
            p.addCircle(new Point(0, 0), r, Color.LIGHTGRAY, 0.5);

        for (int angle = 0; angle < 360; angle += 45) {
            double a = Math.toRadians(angle);
            p.addLine(
                    new Point(0, 0),
                    new Point(10 * Math.cos(a), 10 * Math.sin(a)),
                    Color.LIGHTGRAY, 0.5
            );
        }

        if (mp.getExpr() != null) {
    p.addCurve(new PolarIterator(mp, false), Color.BLUE, 0.1);
}

if (mp.getDerivative() != null) {
    p.addCurve(new PolarIterator(mp, true), Color.RED, 0.1);
}

    }

    private static class PolarIterator implements Point.Iterator {
        private final MathPlot mp;
        private final boolean derivative;
        private double x = 0;

        public PolarIterator(MathPlot mp, boolean derivative) {
            this.mp = mp;
            this.derivative = derivative;
        }

        @Override public void reset() { x = 0; }
        @Override public boolean hasNext() { return x <= Math.PI * 2; }

        @Override
        public Point nextPoint() {
            double r = derivative ? mp.evaluateDerivative(x)
                                  : mp.evaluate(x);
            Point p = new Point(r * Math.cos(x), r * Math.sin(x));
            x += 0.01;
            return p;
        }

        @Override public boolean hasBreak() { return false; }
    }
}
