package MathPlot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import MathPlot.MathPlot.AreaType;
import MathPlot.MathPlot.ExpressionFormat;
import MathPlot.MathPlot.PlotType;

public class AdditionalTesting {

    /* ===================== EXPRESSION SETUP ===================== */

    @Test
    void testSetExpressionAOSAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("sin(x)", ExpressionFormat.AOS);
        assertNotNull(mp.getExpr());
        assertNotNull(mp.getDerivative());
    }

    @Test
    void testSetExpressionRPNAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x sin", ExpressionFormat.RPN);
        assertEquals(0.0, mp.evaluate(0.0), 1e-6);
    }

    @Test
    void testInvalidExpressionThrowsAdditional() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class, () ->
                mp.setExpression("sin(", ExpressionFormat.AOS)
        );
    }

    /* ===================== EVALUATION ===================== */

    @Test
    void testEvaluateSimpleAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x^2", ExpressionFormat.AOS);
        assertEquals(4.0, mp.evaluate(2.0), 1e-6);
    }

    @Test
    void testEvaluateDerivativeAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x^2", ExpressionFormat.AOS);
        assertEquals(4.0, mp.evaluateDerivative(2.0), 1e-6);
    }

    /* ===================== AREA ===================== */

    @Test
    void testAreaRectangularAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);
        double area = mp.area(0, 10, AreaType.Rectangular);
        assertTrue(area > 0);
    }

    @Test
    void testAreaTrapezoidalAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);
        double area = mp.area(0, 10, AreaType.Trapezoidal);
        assertTrue(area > 0);
    }

    @Test
    void testAreaDefaultRangeAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);
        assertDoesNotThrow(() -> mp.area(AreaType.Trapezoidal));
    }

    /* ===================== PRINTING ===================== */

    @Test
    void testPrintAOSAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x^2", ExpressionFormat.AOS);
        List<String> out = mp.print(ExpressionFormat.AOS);
        assertEquals(2, out.size());
    }

    @Test
    void testPrintRPNAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x x *", ExpressionFormat.RPN);
        List<String> out = mp.print(ExpressionFormat.RPN);
        assertEquals(2, out.size());
    }

    /* ===================== SAMPLING ===================== */

    @Test
    void testSampleExprIteratorAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);

        Point.Iterator it = mp.sampleExpr(0, 1, 0.2);
        assertTrue(it.hasNext());

        Point p = it.nextPoint();
        assertEquals(0.0, p.x(), 1e-6);
        assertEquals(0.0, p.y(), 1e-6);
    }

    @Test
    void testSampleDerivIteratorAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x^2", ExpressionFormat.AOS);

        Point.Iterator it = mp.sampleDeriv(1, 2, 0.5);
        assertTrue(it.hasNext());
    }

    /* ===================== SIMPLIFICATION PATH ===================== */

    @Test
    void testSimplificationTriggeredByPrintAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x * 1", ExpressionFormat.RPN);
        List<String> out = mp.print(ExpressionFormat.AOS);
        assertFalse(out.isEmpty());
    }

    /* ===================== STRATEGY DISPATCH ===================== */

    @Test
    void testPlotStrategyDispatchAdditional() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);

        // We only check that dispatch does not throw.
        assertDoesNotThrow(() -> mp.plot(null, PlotType.Cartesian));
    }
}
