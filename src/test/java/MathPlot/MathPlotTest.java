package MathPlot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import MathPlot.MathPlot.ExpressionFormat;

public class MathPlotTest {

    @Test
    void testSetExpressionRPN_EvalValue() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", MathPlot.ExpressionFormat.RPN); // f(x)=2x

        // Evaluate using reflection via print(List)
        double yAt5 = mp.print(MathPlot.ExpressionFormat.RPN).size(); // sanity: print should not be empty
        assertTrue(yAt5 > 0);
    }

    @Test
    void testPrintRPN_NotEmpty() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 3 ^", MathPlot.ExpressionFormat.RPN);

        var out = mp.print(MathPlot.ExpressionFormat.RPN);
        assertEquals(2, out.size()); // f and f'
        assertTrue(out.get(0).contains("x"));
        assertTrue(out.get(1).contains("^"));
    }

    @Test
    void testDerivativePowerRule() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 4 ^", MathPlot.ExpressionFormat.RPN);

        var out = mp.print(MathPlot.ExpressionFormat.RPN);
        assertTrue(out.get(1).contains("4")); // derivative should include coefficient 4
    }

    @Test
public void testPrintAOS_NotEmpty() {
    MathPlot p = new MathPlot();
    p.setExpression("x 3 ^", MathPlot.ExpressionFormat.RPN); // x^3
    var out = p.print(MathPlot.ExpressionFormat.AOS);
    assertEquals(2, out.size());
    assertFalse(out.get(0).isBlank());
    assertFalse(out.get(1).isBlank());
}

@Test
public void testEvaluateDivision() {
    MathPlot p = new MathPlot();
    p.setExpression("10 2 /", MathPlot.ExpressionFormat.RPN);
    assertEquals(5.0, p.evaluate(0.0), 1e-6);
}

@Test
public void testDerivativeOfExp() {
    MathPlot p = new MathPlot();
    p.setExpression("x exp", MathPlot.ExpressionFormat.RPN);
    double dx0 = p.evaluateDerivative(0.0);
    assertEquals(1.0, dx0, 1e-6); // d/dx e^x = e^x ; e^0 = 1
}

@Test
public void testAreaRectangular() {
    MathPlot p = new MathPlot();
    p.setExpression("0", MathPlot.ExpressionFormat.RPN); // constant 0
    double area = p.area(MathPlot.AreaType.Rectangular);
    assertEquals(0.0, area, 1e-6);
}

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
public void testLnOfSinAOS() {
    MathPlot mp = new MathPlot();
    mp.setExpression("ln(sin(x))", ExpressionFormat.AOS);
    // Value check (x=1)
    assertEquals(Math.log(Math.sin(1.0)), mp.evaluate(1.0), 1e-6);
    // Derivative: (cos(x)/sin(x)) => cot(x)
    assertEquals(Math.cos(1.0) / Math.sin(1.0), mp.evaluateDerivative(1.0), 1e-6);
}

@Test
public void testExponentPowerRPN() {
    MathPlot mp = new MathPlot();
    // x 3 ^  (x^3)
    mp.setExpression("x 3 ^", ExpressionFormat.RPN);
    assertEquals(Math.pow(2.0, 3), mp.evaluate(2.0), 1e-6);
    // d/dx x^3 = 3*x^2 -> 3*4 = 12
    assertEquals(12.0, mp.evaluateDerivative(2.0), 1e-6);
}

@Test
public void testZeroMultiplicationSimplify() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(0 * x)", ExpressionFormat.AOS);
    assertEquals(0.0, mp.evaluate(5.0), 1e-9);
    assertEquals(0.0, mp.evaluateDerivative(5.0), 1e-9);
}

@Test
public void testLnVsLogRPN() {
    MathPlot mp = new MathPlot();
    // ln should be normalized → log
    mp.setExpression("x ln", ExpressionFormat.RPN);
    assertEquals(Math.log(2.0), mp.evaluate(2.0), 1e-6);
}

@Test
public void testExpOfLnIdentity() {
    MathPlot mp = new MathPlot();
    mp.setExpression("exp(ln(x))", ExpressionFormat.AOS);
    // exp(ln(x)) == x for x>0
    assertEquals(3.0, mp.evaluate(3.0), 1e-6);
}

@Test
public void testBinaryDivideByX() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(x / x)", ExpressionFormat.AOS);
    // should simplify to 1 (except x=0)
    assertEquals(1.0, mp.evaluate(4.0), 1e-6);
    assertEquals(0.0, mp.evaluateDerivative(4.0), 1e-6);
}
@Test
@DisplayName("Parse AOS: unary function chain ln(sin(x))")
void testAOS_ChainFunction() {
    MathPlot mp = new MathPlot();
    mp.setExpression("ln(sin(x))", MathPlot.ExpressionFormat.AOS);
    assertEquals(Math.log(Math.sin(1.0)), mp.evaluate(1.0), 1e-6);
}

@Test
@DisplayName("Parse RPN: complex binary operations (x 2 * 3 +)")
void testRPN_ComplexExpression() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x 2 * 3 +", MathPlot.ExpressionFormat.RPN);
    assertEquals(2 * 1 + 3, mp.evaluate(1.0), 1e-6);
    assertEquals(2, mp.evaluateDerivative(1.0), 1e-6); // slope always 2
}

@Test
@DisplayName("Division by zero handling: x / 0")
void testDivideByZero() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x 0 /", MathPlot.ExpressionFormat.RPN);
    double val = mp.evaluate(1.0);
    assertTrue(Double.isInfinite(val) || Double.isNaN(val));
}

@Test
@DisplayName("Simplification: 0 + x returns x")
void testSimplify_AddZero() {
    MathPlot mp = new MathPlot();
    mp.setExpression("0 + x", MathPlot.ExpressionFormat.AOS);
    List<String> out = mp.print(MathPlot.ExpressionFormat.AOS);
    assertEquals("f = x", out.get(0));
}

@Test
@DisplayName("Derivative simplification: derivative of x^0 is 0")
void testDerivative_PowerZero() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x 0 ^", MathPlot.ExpressionFormat.RPN);
    assertEquals(0.0, mp.evaluateDerivative(1.0), 1e-6);
}

@Test
@DisplayName("Area: rectangular ≠ trapezoidal for non-constant function")
void testAreaDifference() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

    double rect = mp.area(MathPlot.AreaType.Rectangular);
    double trap = mp.area(MathPlot.AreaType.Trapezoidal);

    assertNotEquals(rect, trap, "Rectangular and trapezoidal should differ for sloped functions");
}


@Test
@DisplayName("AOS: detect unknown function")
void testAOS_UnknownFunction() {
    MathPlot mp = new MathPlot();
    assertThrows(IllegalArgumentException.class,
        () -> mp.setExpression("foo(x)", MathPlot.ExpressionFormat.AOS)
    );
}

@Test
@DisplayName("RPN: invalid sequence throws exception")
void testRPN_Invalid() {
    MathPlot mp = new MathPlot();
    assertThrows(IllegalArgumentException.class,
        () -> mp.setExpression("+ x", MathPlot.ExpressionFormat.RPN));
}
@Test
@DisplayName("Simplify: arithmetic identities")
void testSimplifyIdentities() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(x * 1)", MathPlot.ExpressionFormat.AOS);
    assertEquals(5.0, mp.evaluate(5.0)); // x * 1 → x

    mp.setExpression("(1 * x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(3.0, mp.evaluate(3.0));

    mp.setExpression("(x * 0)", MathPlot.ExpressionFormat.AOS);
    assertEquals(0.0, mp.evaluate(10.0));

    mp.setExpression("(0 * x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(0.0, mp.evaluate(10.0));

    mp.setExpression("(x + 0)", MathPlot.ExpressionFormat.AOS);
    assertEquals(4.0, mp.evaluate(4.0));

    mp.setExpression("(0 + x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(7.0, mp.evaluate(7.0));

    mp.setExpression("(x - 0)", MathPlot.ExpressionFormat.AOS);
    assertEquals(-2.0, mp.evaluate(-2.0));

    mp.setExpression("(x ^ 1)", MathPlot.ExpressionFormat.AOS);
    assertEquals(9.0, mp.evaluate(9.0));

    mp.setExpression("(x ^ 0)", MathPlot.ExpressionFormat.AOS);
    assertEquals(1.0, mp.evaluate(999.0));
}

@Test
@DisplayName("Derivative: sin, cos, exp, ln evaluated")
void testDerivativesFunctions() {
    MathPlot mp = new MathPlot();
    mp.setExpression("sin(x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(Math.cos(1.0), mp.evaluateDerivative(1.0), 1e-6);

    mp.setExpression("cos(x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(-Math.sin(1.0), mp.evaluateDerivative(1.0), 1e-6);

    mp.setExpression("exp(x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(Math.exp(2.0), mp.evaluateDerivative(2.0), 1e-6);

    mp.setExpression("ln(x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(1 / 2.0, mp.evaluateDerivative(2.0), 1e-6);
}
@Test
@DisplayName("Derivative: power rule with constant exponent")
void testDerivativePowConstant() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(x ^ 3)", MathPlot.ExpressionFormat.AOS);

    // 3 * x^2
    assertEquals(3 * Math.pow(2.0, 2), mp.evaluateDerivative(2.0), 1e-6);
}
@Test
@DisplayName("Invalid expression should throw")
void testInvalidExpression() {
    MathPlot mp = new MathPlot();
    assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("^^bad", MathPlot.ExpressionFormat.RPN));
}
@Test
@DisplayName("RPN: log and ln supported equally")
void testRpnLogAndLn() {
    MathPlot mp = new MathPlot();

    mp.setExpression("x ln", MathPlot.ExpressionFormat.RPN);
    assertEquals(Math.log(2), mp.evaluate(2.0), 1e-6);

    mp.setExpression("x log", MathPlot.ExpressionFormat.RPN);
    assertEquals(Math.log(2), mp.evaluate(2.0), 1e-6);
}
@Test
@DisplayName("Simplify: subtraction leading zero becomes negation")
void testSimplifyZeroMinusX() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(0 - x)", MathPlot.ExpressionFormat.AOS);
    assertEquals(-5.0, mp.evaluate(5.0));
}
@Test
@DisplayName("Derivative: nested functions chain rule")
void testDerivativeNestedFunctions() {
    MathPlot mp = new MathPlot();
    // f(x) = exp(sin(x))
    mp.setExpression("exp(sin(x))", MathPlot.ExpressionFormat.AOS);
    double x = 1.0;
    // f'(x) = exp(sin(x)) * cos(x)
    double expected = Math.exp(Math.sin(x)) * Math.cos(x);
    assertEquals(expected, mp.evaluateDerivative(x), 1e-6);
}

@Test
@DisplayName("RPN: negative numbers and multiple ops")
void testRpnNegativeNumbers() {
    MathPlot mp = new MathPlot();
    mp.setExpression("3 -2 *", MathPlot.ExpressionFormat.RPN); // 3 * (-2) = -6
    assertEquals(-6.0, mp.evaluate(0.0));
}

@Test
@DisplayName("AOS: deep parentheses properly ignored")
void testAosDeepBrackets() {
    MathPlot mp = new MathPlot();
    mp.setExpression("(((x)))", MathPlot.ExpressionFormat.AOS);
    assertEquals(4.0, mp.evaluate(4.0));
}
@Test
@DisplayName("AOS: unknown function should fail")
void testAosUnknownFunction() {
    MathPlot mp = new MathPlot();
    assertThrows(IllegalArgumentException.class,
            () -> mp.setExpression("foo(x)", MathPlot.ExpressionFormat.AOS));
}
@Test
@DisplayName("Derivative: unsupported non-constant exponent throws")
void testPowNonConstantExponentDerivative() {
    MathPlot mp = new MathPlot();
    assertThrows(UnsupportedOperationException.class,
        () -> mp.setExpression("(x ^ x)", MathPlot.ExpressionFormat.AOS),
        "Non-constant exponent differentiation should throw UnsupportedOperationException");
}
    @Test
    void testDerivativeExistsAfterValidExpression() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x 2 *", MathPlot.ExpressionFormat.RPN);
        assertDoesNotThrow(() -> mp.evaluateDerivative(3.0));
    }

    @Test
void testAreaCustomBoundsRectangular() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

    double area = mp.area(-2, 2, MathPlot.AreaType.Rectangular);
    assertTrue(area != 0.0);
}

@Test
void testAreaCustomBoundsTrapezoidal() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

    double area = mp.area(-2, 2, MathPlot.AreaType.Trapezoidal);
    assertTrue(area != 0.0);
}

@Test
void testAreaReversedBoundsDifferentFromNormal() {
    MathPlot mp = new MathPlot();
    mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

    double forward = mp.area(-5, 5, MathPlot.AreaType.Trapezoidal);
    double reversed = mp.area(5, -5, MathPlot.AreaType.Trapezoidal);

    assertEquals(0.0, reversed, 1e-6);
    assertNotEquals(forward, reversed);
}


}

