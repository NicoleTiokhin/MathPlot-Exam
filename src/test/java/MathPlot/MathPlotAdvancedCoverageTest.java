package MathPlot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.scene.canvas.Canvas;

class MathPlotAdvancedCoverageTest {

    // Minimal canvas for JavaFX plotter
    private static class FakeCanvas extends Canvas {
        FakeCanvas() {
            super(400, 300);
        }
    }

    // ─────────────────────────────────────────────
    // BASIC PLOT EXECUTION
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("plot() executes without crashing (Cartesian)")
    void testPlotExecution() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", MathPlot.ExpressionFormat.RPN);

        Canvas canvas = new FakeCanvas();
        assertDoesNotThrow(() ->
                mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }

    @Test
    @DisplayName("plot() with null expression draws axes only")
    void testPlotNullExpression() {
        MathPlot mp = new MathPlot();
        Canvas canvas = new FakeCanvas();

        assertDoesNotThrow(() ->
                mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }

    @Test
    @DisplayName("SampleIterator full traversal (implicit via plot)")
    void testSampleIteratorCoverage() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas canvas = new FakeCanvas();
        assertDoesNotThrow(() ->
                mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );
    }

    // ─────────────────────────────────────────────
    // SIMPLIFICATION COVERAGE
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Simplify exponent 1 → returns base")
    void testSimplifyPow1() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x ^ 1)", MathPlot.ExpressionFormat.AOS);
        assertEquals(7.0, mp.evaluate(7.0));
    }

    @Test
    @DisplayName("AOS exponent zero simplifies to 1")
    void testAosZeroExponent() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x ^ 0)", MathPlot.ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(123.0), 1e-9);
    }

    // ─────────────────────────────────────────────
    // AREA COVERAGE
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Reversed bounds return area = 0")
    void testAreaReversed() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        double area = mp.area(1, -1, MathPlot.AreaType.Rectangular);
        assertEquals(0.0, area, 1e-9);
    }

    // ─────────────────────────────────────────────
    // RPN VALIDATION COVERAGE
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("RPN leftover tokens causes exception")
    void testRpnLeftoverStack() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
                () -> mp.setExpression("x x + 2", MathPlot.ExpressionFormat.RPN)
        );
    }

    // ─────────────────────────────────────────────
    // AOS PARSER STRESS CASE
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Deep AOS parentheses parsing works")
    void testDeepAos() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(((sin((x)))))", MathPlot.ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluate(0.0), 1e-9);
    }

    // ─────────────────────────────────────────────
    // CROSS-MODULE PLOT / PRINT COMBINATION
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("Polar plot + print(RPN) does not crash")
    void testPrintRpnAfterPolarPlotDoesNotCrash() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x sin", MathPlot.ExpressionFormat.RPN);

        Canvas c = new FakeCanvas();
        assertDoesNotThrow(() ->
                mp.plot(c, MathPlot.PlotType.Polar)
        );

        var out = mp.print(MathPlot.ExpressionFormat.RPN);
        assertEquals(2, out.size()); // f and f'
    }
}
