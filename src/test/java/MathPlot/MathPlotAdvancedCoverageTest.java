package MathPlot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.scene.canvas.Canvas;

class MathPlotAdvancedCoverageTest {

    private static class FakeCanvas extends Canvas {
        FakeCanvas() {
            super(400, 300); // small but non-zero size
        }
    }

    @Test
    @DisplayName("plot() executes without crashing")
    void testPlotExecution() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", MathPlot.ExpressionFormat.RPN);

        Canvas canvas = new FakeCanvas();
        assertDoesNotThrow(() ->
                mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }

    @Test
    @DisplayName("plot() with null expr renders axes only")
    void testPlotNullExpression() {
        MathPlot mp = new MathPlot();
        Canvas canvas = new FakeCanvas();
        assertDoesNotThrow(() ->
                mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }

    // ---- SampleIterator test without direct access ----
    @Test
    @DisplayName("plot() triggers SampleIterator iteration fully")
    void testSampleIteratorCoverage() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas canvas = new FakeCanvas();
        // If SampleIterator has a bug → plot() would crash
        assertDoesNotThrow(() ->
                mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }

    @Test
    @DisplayName("Simplify exponent 1 → base")
    void testSimplifyPow1() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x ^ 1)", MathPlot.ExpressionFormat.AOS);
        assertEquals(7.0, mp.evaluate(7.0));
    }

    @DisplayName("Area reversed interval should not crash and return 0")
@Test
void testAreaReversed() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x", MathPlot.ExpressionFormat.AOS);
    double area = mp.area(1, -1, MathPlot.AreaType.Rectangular);

    assertEquals(0.0, area, 1e-9, "Reversed bounds should produce 0 area");
}


    @Test
    @DisplayName("RPN leftover tokens → error")
    void testRpnLeftoverStack() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
                () -> mp.setExpression("x x + 2", MathPlot.ExpressionFormat.RPN));
    }

    @Test
    @DisplayName("Deep AOS parse works")
    void testDeepAos() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(((sin((x)))))", MathPlot.ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluate(0.0), 1e-9);
    }


    @Test
void testPrintRpnAfterPolarPlotDoesNotCrash() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x sin", MathPlot.ExpressionFormat.RPN);

    Canvas c = new FakeCanvas();
    assertDoesNotThrow(() ->
        mp.plot(c, MathPlot.PlotType.Polar)
    );

    var out = mp.print(MathPlot.ExpressionFormat.RPN);
    assertEquals(2, out.size());
}

}
