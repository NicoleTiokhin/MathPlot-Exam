package MathPlot;

import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MathPlotPlotterDeepCoverageTest {

    private Canvas C() { return new Canvas(600, 600); }

    @Test
    void testCartesianPlotCoversPlotter() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas c = C();

        // triggers Plotter.Cartesian constructor + draw grid + draw axes + evaluate samples
        assertDoesNotThrow(() -> mp.plot(c, MathPlot.PlotType.Cartesian));

        // resizing forces PlotterBase recompute and internal drawing
        c.setWidth(800);
        c.setHeight(400);

        assertDoesNotThrow(() -> mp.plot(c, MathPlot.PlotType.Cartesian));
    }

    @Test
    void testPolarPlotCoversPlotter() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x sin", MathPlot.ExpressionFormat.RPN);

        Canvas c = C();

        // triggers Plotter.Polar constructor + curve drawing
        assertDoesNotThrow(() -> mp.plot(c, MathPlot.PlotType.Polar));
        assertDoesNotThrow(() -> mp.plot(c, MathPlot.PlotType.Polar));
    }
}
