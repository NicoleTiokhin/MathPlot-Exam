package MathPlot;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MathPlotFeatureTest {

    // Parsing Tests (RPN + AOS)
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
        assertEquals(0.0, mp.evaluate(0.0), 1e-6);
    }

    @Test
    void testAOSParseLog() {
        MathPlot mp = new MathPlot();
        mp.setExpression("log(x)", MathPlot.ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(Math.E), 1e-6);
    }

    @Test
    void testAOSParseLn() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(x)", MathPlot.ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(Math.E), 1e-6);
    }

    // Derivative tests
    @Test
    void testPowerRule() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 3 ^", MathPlot.ExpressionFormat.RPN);
        assertEquals(3.0, mp.evaluateDerivative(1.0), 1e-6);
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
        assertEquals(1.0, mp.evaluateDerivative(0.0), 1e-6);
    }

    @Test
    void testDerivLn() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(x)", MathPlot.ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluateDerivative(1.0), 1e-6);
    }

    // Simplification Tests
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
        assertEquals("f = 1.0", out.get(0));
    }

    @Test
    void testSimplifyAddZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 0 +", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals("f = x", out.get(0));
    }

    // Area Tests
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

    // Print Tests
    @Test
    void testPrintRPNNotEmpty() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", MathPlot.ExpressionFormat.RPN);
        var out = mp.print(MathPlot.ExpressionFormat.RPN);
        assertEquals(2, out.size());
        assertFalse(out.get(0).isBlank());
        assertFalse(out.get(1).isBlank());
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

    // Invalid expression tests
    @Test
    void testAOSIncompleteExpressionFails() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("sin()", MathPlot.ExpressionFormat.AOS));
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
            () -> mp.setExpression("2 2 2 +", MathPlot.ExpressionFormat.RPN));
    }

    @Test
    void testRpnUnknownTokenThrows() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("x y ?", MathPlot.ExpressionFormat.RPN));
    }
}
