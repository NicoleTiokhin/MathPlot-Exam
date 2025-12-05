package MathPlot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import javafx.scene.canvas.Canvas;

public class MathPlotPlotTest {

    @Test
    void testPlotDoesNotCrash() {
        Canvas canvas = new Canvas(400, 300);
        MathPlot mp = new MathPlot();
        mp.setExpression("sin(x)", MathPlot.ExpressionFormat.AOS);

        // Initial plot
        assertDoesNotThrow(() ->
            mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );

        // Resize canvas forces re-render
        canvas.setWidth(600);
        canvas.setHeight(400);

        // Plot again after resize → should still not crash
        assertDoesNotThrow(() ->
            mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }
        @Test
    void testPolarNegativeRadiusDoesNotCrash() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x sin 0 -", MathPlot.ExpressionFormat.RPN);

        Canvas c = new Canvas(400, 400);
        assertDoesNotThrow(() ->
            mp.plot(c, MathPlot.PlotType.Polar)
        );
    }

    @Test
    void testPolarHighOscillationDoesNotCrash() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 10 * sin", MathPlot.ExpressionFormat.RPN);


        Canvas c = new Canvas(400, 400);
        assertDoesNotThrow(() ->
            mp.plot(c, MathPlot.PlotType.Polar)
        );
    }
    @Test
void testPlotWithVerySmallCanvasDoesNotCrash() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x", MathPlot.ExpressionFormat.AOS);
    Canvas canvas = new Canvas(1, 1);  // extremely small

    assertDoesNotThrow(() ->
        mp.plot(canvas, MathPlot.PlotType.Cartesian)
    );
}

}
