package MathPlot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MathPlotFeatureTests {

    // ───────────────────────────────
    // Parsing Tests (RPN + AOS)
    // ───────────────────────────────
    @Test
    void testRPNParseSimpleAdd() {
        MathPlot mp = new MathPlot();
        mp.setExpression("2 3 +", MathPlot.ExpressionFormat.RPN);
        assertEquals(5.0, mp.evaluate(0.0), 1e-6);
    }

    @Test
    void testAOSParseSin() {
        MathPlot mp = new MathPlot();
        mp.setExpression("sin(x)", MathPlot.ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluate(0.0), 1e-6); // sin(0) = 0
    }

    @Test
    void testAOSParseLog() {
        MathPlot mp = new MathPlot();
        mp.setExpression("log(x)", MathPlot.ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(Math.E), 1e-6); // ln(e) = 1
    }

    @Test
    void testAOSParseLn() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(x)", MathPlot.ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(Math.E), 1e-6);
    }

    // ───────────────────────────────
    // Derivative Tests
    // ───────────────────────────────
    @Test
    void testPowerRule() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 3 ^", MathPlot.ExpressionFormat.RPN); // x^3
        assertEquals(3.0, mp.evaluateDerivative(1.0), 1e-6); // 3x^2 at x=1
    }

    @Test
    void testDerivSin() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x sin", MathPlot.ExpressionFormat.RPN);
        assertEquals(Math.cos(0.0), mp.evaluateDerivative(0.0), 1e-6);
    }

    @Test
    void testDerivCos() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x cos", MathPlot.ExpressionFormat.RPN);
        assertEquals(-Math.sin(0.0), mp.evaluateDerivative(0.0), 1e-6);
    }

    @Test
    void testDerivExp() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x exp", MathPlot.ExpressionFormat.RPN);
        assertEquals(1.0, mp.evaluateDerivative(0.0), 1e-6); // e^0 = 1
    }

    @Test
    void testDerivLn() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x log", MathPlot.ExpressionFormat.RPN);
        assertEquals(1.0, mp.evaluateDerivative(Math.E), 1e-6); // d/dx ln x = 1/x
    }

    // ───────────────────────────────
    // Simplification Tests
    // ───────────────────────────────
    @Test
    void testSimplifyMulByOne() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 1 *", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals("f = x", out.get(0));
    }

    @Test
    void testSimplifyPowZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 0 ^", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals("f = 1.0", out.get(0)); // Const prints as 1.0
    }

    @Test
    void testSimplifyAddZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 0 +", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals("f = x", out.get(0));
    }

    // ───────────────────────────────
    // Area Tests
    // ───────────────────────────────
    @Test
    void testAreaRectangularZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("0", MathPlot.ExpressionFormat.RPN);
        assertEquals(0.0, mp.area(MathPlot.AreaType.Rectangular), 1e-6);
    }

    @Test
    void testAreaTrapezoidalZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("0", MathPlot.ExpressionFormat.RPN);
        assertEquals(0.0, mp.area(MathPlot.AreaType.Trapezoidal), 1e-6);
    }

    // ───────────────────────────────
    // Print Tests
    // ───────────────────────────────
    @Test
    void testPrintRPNNotEmpty() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.RPN);
        assertEquals(2, out.size());
        assertTrue(out.get(0).contains("x"));
        assertTrue(out.get(1).contains("2"));
    }

    @Test
    void testPrintAOSNotBlank() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 3 ^", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals(2, out.size());
        assertFalse(out.get(0).isBlank());
        assertFalse(out.get(1).isBlank());
    }

        @Test
    void testAOSIncompleteExpressionFails() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
                () -> mp.setExpression("sin()", MathPlot.ExpressionFormat.AOS));
    }

    @Test
    void testAOSRepeatedOperatorFails() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
                () -> mp.setExpression("x + + 2", MathPlot.ExpressionFormat.AOS));
    }

    @Test
    void testRPNTooFewOperandsFails() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
                () -> mp.setExpression("+", MathPlot.ExpressionFormat.RPN));
    }

    @Test
    void testRPNTooManyOperandsFails() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
                () -> mp.setExpression("x x 2", MathPlot.ExpressionFormat.RPN));
    }
    @Test
void testRpnUnknownTokenThrows() {
    MathPlot mp = new MathPlot();
    assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("x y ?", MathPlot.ExpressionFormat.RPN));
}
@Test
void testUnsupportedPowDerivativeCase() {
    MathPlot mp = new MathPlot();
    assertThrows(UnsupportedOperationException.class,
        () -> mp.setExpression("(x ^ x)", MathPlot.ExpressionFormat.AOS));
}


}
