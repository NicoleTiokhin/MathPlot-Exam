package MathPlot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MathPlotExtraTests {

    // Domain errors: ln invalid ranges
    @Test
    void testLnNegativeDomain() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(x)", MathPlot.ExpressionFormat.AOS);
        double v = mp.evaluate(-1.0);
        assertTrue(Double.isNaN(v) || Double.isInfinite(v));
    }

    @Test
    void testLnZeroDomain() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(x)", MathPlot.ExpressionFormat.AOS);
        assertTrue(Double.isInfinite(mp.evaluate(0.0)));
    }

    // Print formatting edge cases
    @Test
    void testPrintConstantMultiFormat() {
        MathPlot mp = new MathPlot();
        mp.setExpression("5", MathPlot.ExpressionFormat.RPN);
        var aos = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals("f = 5.0", aos.get(0));
        assertFalse(aos.get(1).isBlank());
    }

    @Test
    void testPrintNoExtraSpaces() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);
        var printed = mp.print(MathPlot.ExpressionFormat.AOS);
        assertFalse(printed.get(0).contains("  "));
    }

    // Unsupported derivative
    @Test
    void testPowNonConstantExponentDerivative() {
        MathPlot mp = new MathPlot();
        assertThrows(UnsupportedOperationException.class, () ->
                mp.setExpression("(x ^ (2*x))", MathPlot.ExpressionFormat.AOS));
    }

    // Constant function derivative
    @Test
    void testDerivConstantFunction() {
        MathPlot mp = new MathPlot();
        mp.setExpression("10", MathPlot.ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluateDerivative(5.0), 1e-6);
    }

    // Check print & derivative exist for complicated unary chain
    @Test
    void testPrintChainFunctions() {
        MathPlot mp = new MathPlot();
        mp.setExpression("exp(sin(x))", MathPlot.ExpressionFormat.AOS);
        var out = mp.print(MathPlot.ExpressionFormat.AOS);
        assertEquals(2, out.size());
        assertFalse(out.get(0).isBlank());
        assertFalse(out.get(1).isBlank());
    }

        @Test
    void testSimplifyDeepZeroMultiplication() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(0 * (x + 5))", MathPlot.ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluate(10.0));
    }

    @Test
    void testSimplifyMultiIdentityChain() {
        MathPlot mp = new MathPlot();
        mp.setExpression("((x + 0) * 1)", MathPlot.ExpressionFormat.AOS);
        assertEquals(9.0, mp.evaluate(9.0));
    }

    @Test
void testSimplifySubtractSelf() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(x - x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(0.0, mp.evaluate(3.0), 1e-9);
}

@Test
void testSimplifyPowerSimplification() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(5 ^ 0)", MathPlot.ExpressionFormat.AOS);
    assertEquals(1.0, mp.evaluate(5.0), 1e-9);
}
@Test
void testPrintWithoutExpressionReturnsEmpty() {
    MathPlot mp = new MathPlot();
    var out = mp.print(MathPlot.ExpressionFormat.AOS);
    assertTrue(out.isEmpty());
}


}
