package MathPlot.plot;

import MathPlot.MathPlot.PlotType;

public final class PlotStrategyFactory {

    private PlotStrategyFactory() {}

    public static PlotStrategy create(PlotType type) {
        return switch (type) {
            case Cartesian -> new CartesianPlotStrategy();
            case Polar -> new PolarPlotStrategy();
        };
    }
}
