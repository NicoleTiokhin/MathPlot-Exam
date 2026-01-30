package MathPlot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import MathPlot.MathPlot.ExpressionFormat;

public class MathPlotTest {

    @Test
    void testSetExpressionRPN_EvalValue() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", ExpressionFormat.RPN);
        assertTrue(mp.print(ExpressionFormat.RPN).size() > 0);
    }

    @Test
    void testPrintRPN_NotEmpty() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 3 ^", ExpressionFormat.RPN);

        var out = mp.print(ExpressionFormat.RPN);
        assertEquals(2, out.size());
        assertTrue(out.get(0).contains("x"));
        assertTrue(out.get(1).contains("^"));
    }

    @Test
    void testDerivativePowerRule() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 4 ^", ExpressionFormat.RPN);

        var out = mp.print(ExpressionFormat.RPN);
        assertTrue(out.get(1).contains("4"));
    }

    @Test
    public void testPrintAOS_NotEmpty() {
        MathPlot p = new MathPlot();
        p.setExpression("x 3 ^", ExpressionFormat.RPN);
        var out = p.print(ExpressionFormat.AOS);
        assertEquals(2, out.size());
        assertFalse(out.get(0).isBlank());
        assertFalse(out.get(1).isBlank());
    }

    @Test
    public void testEvaluateDivision() {
        MathPlot p = new MathPlot();
        p.setExpression("10 2 /", ExpressionFormat.RPN);
        assertEquals(5.0, p.evaluate(0.0), 1e-6);
    }

    @Test
    public void testDerivativeOfExp() {
        MathPlot p = new MathPlot();
        p.setExpression("x exp", ExpressionFormat.RPN);
        assertEquals(1.0, p.evaluateDerivative(0.0), 1e-6);
    }

    @Test
    public void testAreaRectangular() {
        MathPlot p = new MathPlot();
        p.setExpression("0", ExpressionFormat.RPN);
        assertEquals(0.0, p.area(MathPlot.AreaType.Rectangular), 1e-6);
    }

    @Test
    void testSimplifyMulByOne() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 1 *", ExpressionFormat.RPN);
        assertEquals("f = x", mp.print(ExpressionFormat.AOS).get(0));
    }

    @Test
    void testSimplifyPowZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 0 ^", ExpressionFormat.RPN);
        assertEquals("f = 1.0", mp.print(ExpressionFormat.AOS).get(0));
    }

    @Test
    public void testLnOfSinAOS() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(sin(x))", ExpressionFormat.AOS);
        assertEquals(Math.log(Math.sin(1.0)), mp.evaluate(1.0), 1e-6);
        assertEquals(Math.cos(1.0) / Math.sin(1.0), mp.evaluateDerivative(1.0), 1e-6);
    }

    @Test
    public void testExponentPowerRPN() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 3 ^", ExpressionFormat.RPN);

        assertEquals(8.0, mp.evaluate(2.0), 1e-6);
        assertEquals(12.0, mp.evaluateDerivative(2.0), 1e-6);
    }

    @Test
    public void testZeroMultiplicationSimplify() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(0 * x)", ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluate(5.0));
        assertEquals(0.0, mp.evaluateDerivative(5.0));
    }

    @Test
    public void testLnVsLogRPN() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x ln", ExpressionFormat.RPN);
        assertEquals(Math.log(2.0), mp.evaluate(2.0), 1e-6);
    }

    @Test
    public void testExpOfLnIdentity() {
        MathPlot mp = new MathPlot();
        mp.setExpression("exp(ln(x))", ExpressionFormat.AOS);
        assertEquals(3.0, mp.evaluate(3.0), 1e-6);
    }

    @Test
    public void testBinaryDivideByX() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x / x)", ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(4.0), 1e-6);
        assertEquals(0.0, mp.evaluateDerivative(4.0), 1e-6);
    }

    @Test
    void testAOS_ChainFunction() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(sin(x))", ExpressionFormat.AOS);
        assertEquals(Math.log(Math.sin(1.0)), mp.evaluate(1.0), 1e-6);
    }

    @Test
    void testRPN_ComplexExpression() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 * 3 +", ExpressionFormat.RPN);

        assertEquals(5.0, mp.evaluate(1.0), 1e-6);
        assertEquals(2.0, mp.evaluateDerivative(1.0), 1e-6);
    }

    @Test
    void testDivideByZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 0 /", ExpressionFormat.RPN);
        double v = mp.evaluate(1.0);
        assertTrue(Double.isInfinite(v) || Double.isNaN(v));
    }

    @Test
    void testSimplify_AddZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("0 + x", ExpressionFormat.AOS);
        assertEquals("f = x", mp.print(ExpressionFormat.AOS).get(0));
    }

    @Test
    void testDerivative_PowerZero() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 0 ^", ExpressionFormat.RPN);
        assertEquals(0.0, mp.evaluateDerivative(1.0), 1e-6);
    }

    @Test
    void testAreaDifference() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);

        assertNotEquals(
            mp.area(MathPlot.AreaType.Rectangular),
            mp.area(MathPlot.AreaType.Trapezoidal)
        );
    }

    @Test
    void testAOS_UnknownFunction() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("foo(x)", ExpressionFormat.AOS)
        );
    }

    @Test
    void testRPN_Invalid() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("+ x", ExpressionFormat.RPN)
        );
    }

    @Test
    void testSimplifyIdentities() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x * 1)", ExpressionFormat.AOS);
        assertEquals(5.0, mp.evaluate(5.0));

        mp.setExpression("(x * 0)", ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluate(10.0));

        mp.setExpression("(x ^ 1)", ExpressionFormat.AOS);
        assertEquals(9.0, mp.evaluate(9.0));

        mp.setExpression("(x ^ 0)", ExpressionFormat.AOS);
        assertEquals(1.0, mp.evaluate(999.0));
    }

    @Test
    void testDerivativesFunctions() {
        MathPlot mp = new MathPlot();

        mp.setExpression("sin(x)", ExpressionFormat.AOS);
        assertEquals(Math.cos(1.0), mp.evaluateDerivative(1.0), 1e-6);

        mp.setExpression("cos(x)", ExpressionFormat.AOS);
        assertEquals(-Math.sin(1.0), mp.evaluateDerivative(1.0), 1e-6);

        mp.setExpression("exp(x)", ExpressionFormat.AOS);
        assertEquals(Math.exp(2.0), mp.evaluateDerivative(2.0), 1e-6);

        mp.setExpression("ln(x)", ExpressionFormat.AOS);
        assertEquals(0.5, mp.evaluateDerivative(2.0), 1e-6);
    }

    @Test
    void testDerivativePowConstant() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x ^ 3)", ExpressionFormat.AOS);
        assertEquals(12.0, mp.evaluateDerivative(2.0), 1e-6);
    }

    @Test
    void testInvalidExpression() {
        MathPlot mp = new MathPlot();
        assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("^^bad", ExpressionFormat.RPN));
    }

    @Test
    void testRpnLogAndLn() {
        MathPlot mp = new MathPlot();

        mp.setExpression("x ln", ExpressionFormat.RPN);
        assertEquals(Math.log(2), mp.evaluate(2.0), 1e-6);

        mp.setExpression("x log", ExpressionFormat.RPN);
        assertEquals(Math.log(2), mp.evaluate(2.0), 1e-6);
    }

    @Test
    void testSimplifyZeroMinusX() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(0 - x)", ExpressionFormat.AOS);
        assertEquals(-5.0, mp.evaluate(5.0));
    }

    @Test
    void testDerivativeNestedFunctions() {
        MathPlot mp = new MathPlot();
        mp.setExpression("exp(sin(x))", ExpressionFormat.AOS);

        double x = 1.0;
        double expected = Math.exp(Math.sin(x)) * Math.cos(x);

        assertEquals(expected, mp.evaluateDerivative(x), 1e-6);
    }

    @Test
    void testRpnNegativeNumbers() {
        MathPlot mp = new MathPlot();
        mp.setExpression("3 -2 *", ExpressionFormat.RPN);
        assertEquals(-6.0, mp.evaluate(0.0));
    }

    @Test
    void testAosDeepBrackets() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(((x)))", ExpressionFormat.AOS);
        assertEquals(4.0, mp.evaluate(4.0));
    }

    @Test
    void testPowNonConstantExponentDerivative() {
        MathPlot mp = new MathPlot();
        assertThrows(UnsupportedOperationException.class,
            () -> mp.setExpression("(x ^ x)", ExpressionFormat.AOS));
    }

    @Test
    void testDerivativeExistsAfterValidExpression() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", ExpressionFormat.RPN);
        assertDoesNotThrow(() -> mp.evaluateDerivative(3.0));
    }

    @Test
    void testAreaCustomBoundsRectangular() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);
        assertNotEquals(0.0, mp.area(-2, 2, MathPlot.AreaType.Rectangular));
    }

    @Test
    void testAreaCustomBoundsTrapezoidal() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);
        assertNotEquals(0.0, mp.area(-2, 2, MathPlot.AreaType.Trapezoidal));
    }

    @Test
    void testAreaReversedBoundsDifferentFromNormal() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", ExpressionFormat.AOS);
        
        double f = mp.area(-5, 5, MathPlot.AreaType.Trapezoidal);
        double r = mp.area(5, -5, MathPlot.AreaType.Trapezoidal);

        assertEquals(0.0, r, 1e-6);
        assertNotEquals(f, r);
    }

    @Test
    void testDerivativeCompositeDivision() {
        MathPlot mp = new MathPlot();
        mp.setExpression("sin((x / 2))", ExpressionFormat.AOS);

        double x = 3.0;
        double expected = Math.cos(x / 2) * 0.5;

        assertEquals(expected, mp.evaluateDerivative(x), 1e-6);
    }

    @Test
    void testLargeConstantExponentDerivative() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 50 ^", ExpressionFormat.RPN);
        assertTrue(Double.isFinite(mp.evaluateDerivative(2.0)));
    }

    @Test
    void testRpnUnaryBinaryStress() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x sin 3 +", ExpressionFormat.RPN);
        assertEquals(Math.sin(2) + 3, mp.evaluate(2), 1e-6);
    }

    // FIXED VERSION — no getDerivative(), check numeric value instead
    @Test
    void testDerivativeConstant() {
        MathPlot mp = new MathPlot();
        mp.setExpression("5", ExpressionFormat.AOS);
        assertEquals(0.0, mp.evaluateDerivative(10.0), 1e-6);
    }

    // FIXED VERSION — structural string unavailable, numeric evaluation used
    @Test
    void testDerivativeNested() {
        MathPlot mp = new MathPlot();
        mp.setExpression("sin((x * x))", ExpressionFormat.AOS);
        double x = 2.0;
        double expected = Math.cos(x*x) * (2*x);
        assertEquals(expected, mp.evaluateDerivative(x), 1e-6);
    }

    @Test
    void testDerivativeDivision() {
        MathPlot mp = new MathPlot();
        mp.setExpression("(x / 2)", ExpressionFormat.AOS);
        assertEquals(0.5, mp.evaluateDerivative(100.0), 1e-6);
    }

    @Test
    void testEvaluateNegativeX() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 ^", ExpressionFormat.RPN);
        assertEquals(4.0, mp.evaluate(-2), 0.001);
    }

    @Test
    void testEvaluateLogDomainError() {
        MathPlot mp = new MathPlot();
        mp.setExpression("ln(x)", ExpressionFormat.AOS);
        double v = mp.evaluate(-1);
        assertTrue(Double.isNaN(v) || Double.isInfinite(v));
    }
    @Test
void testSetExpressionAOS() {
    MathPlot mp = new MathPlot();
    mp.setExpression("sin(x)", ExpressionFormat.AOS);
    assertEquals(0.0, mp.evaluate(0), 1e-6);
}

@Test
void testSetExpressionRPN() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x sin", ExpressionFormat.RPN);
    assertEquals(0.0, mp.evaluate(0), 1e-6);
}

@Test
void testPrintAOS() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x^2", ExpressionFormat.AOS);
    assertFalse(mp.print(ExpressionFormat.AOS).isEmpty());
}


    @Test
    void testEvaluateLargeX() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x x *", ExpressionFormat.RPN);
        assertEquals(1_000_000.0, mp.evaluate(1000), 0.1);
    }
}
