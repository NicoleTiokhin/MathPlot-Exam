package MathPlot.area;

import MathPlot.MathPlot;

public class AreaCalculator {

    private final MathPlot mp;

    public AreaCalculator(MathPlot mp) {
        this.mp = mp;
    }

    public double compute(double from, double to, MathPlot.AreaType type) {
        double step = 0.01;
        double acc = 0.0;

        for (double x = from; x < to; x += step) {
            double y1 = mp.evaluate(x);
            double y2 = mp.evaluate(x + step);

            switch (type) {
                case Rectangular -> acc += y1 * step;
                case Trapezoidal -> acc += 0.5 * (y1 + y2) * step;
            }
        }

        return acc;
    }
}
