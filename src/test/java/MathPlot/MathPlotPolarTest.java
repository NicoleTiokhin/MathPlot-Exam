package MathPlot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import javafx.scene.canvas.Canvas;

public class MathPlotPolarTest {

    @Test
    void testPolarPlotSimpleDoesNotCrash() {
        MathPlot mp = new MathPlot();
        mp.setExpression("sin(x)", MathPlot.ExpressionFormat.AOS);

        Canvas canvas = new Canvas(500, 500);

        assertDoesNotThrow(() ->
            mp.plot(canvas, MathPlot.PlotType.Polar)
        );
    }

    @Test
    void testPolarPlotZeroFunction() {
        MathPlot mp = new MathPlot();
        mp.setExpression("0", MathPlot.ExpressionFormat.RPN);

        Canvas canvas = new Canvas(400, 400);

        // Should draw circles/grid only (no crash)
        assertDoesNotThrow(() ->
            mp.plot(canvas, MathPlot.PlotType.Polar)
        );
    }
}