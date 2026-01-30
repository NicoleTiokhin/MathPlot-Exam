package MathPlot.plot;

import MathPlot.MathPlot;
import MathPlot.PlotterInterface;
import MathPlot.Point;
import javafx.scene.paint.Color;

public class CartesianPlotStrategy implements PlotStrategy {

    @Override
    public void plot(MathPlot mp, PlotterInterface p) {

        // Draw axes
        p.addLine(new Point(-10, 0), new Point(10, 0), Color.GRAY, 0.05);
        p.addLine(new Point(0, -10), new Point(0, 10), Color.GRAY, 0.05);

        if (mp.getExpr() == null) return;

        // f(x)
        Point.Iterator it = mp.sampleExpr(-10, 10, 0.1);
        it.reset();
        while (it.hasNext()) {
            Point a = it.nextPoint();
            if (!it.hasNext()) break;
            Point b = it.nextPoint();
            p.addLine(a, b, Color.BLUE, 0.1);
        }

        // f'(x)
        Point.Iterator dit = mp.sampleDeriv(-10, 10, 0.1);
        dit.reset();
        while (dit.hasNext()) {
            Point a = dit.nextPoint();
            if (!dit.hasNext()) break;
            Point b = dit.nextPoint();
            p.addLine(a, b, Color.RED, 0.1);
        }
    }
}
