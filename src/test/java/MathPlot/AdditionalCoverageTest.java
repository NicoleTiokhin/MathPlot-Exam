package MathPlot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import MathPlot.derivative.DerivativeCalculator;
import MathPlot.derivative.Differentiator;
import MathPlot.expression.ExpressionContext;
import MathPlot.ast.Expr;
import MathPlot.ast.Const;
import MathPlot.ast.Var;
import MathPlot.plot.PlotStrategy;
import MathPlot.plot.CartesianPlotStrategy;
import MathPlot.plot.PolarPlotStrategy;

import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;

public class AdditionalCoverageTest {

    /* ===================== DERIVATIVE PACKAGE ===================== */

    @Test
    public void testDerivativeCalculatorEvaluate() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x ^ 2", MathPlot.ExpressionFormat.AOS);

        DerivativeCalculator dc = new DerivativeCalculator(mp);
        double value = dc.evaluate(3.0);

        assertEquals(6.0, value, 0.001);
    }

    @Test
    public void testDifferentiator() {
        Expr e = new Var();
        Expr d = Differentiator.differentiate(e);

        assertNotNull(d);
        assertEquals(1.0, d.eval(0), 0.001);
    }

    /* ===================== EXPRESSION PACKAGE ===================== */

    @Test
    public void testExpressionContext() {
        Expr expr = new Var();
        Expr deriv = new Const(1);

        ExpressionContext ctx = new ExpressionContext(expr, deriv);

        assertNotNull(ctx.getExpr());
        assertNotNull(ctx.getDerivative());
        assertEquals(5.0, ctx.getExpr().eval(5), 0.001);
        assertEquals(1.0, ctx.getDerivative().eval(10), 0.001);
    }

    /* ===================== MATHPLOT CORE ===================== */

    @Test
    public void testEvaluateAndDerivative() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x * x", MathPlot.ExpressionFormat.AOS);

        assertEquals(4.0, mp.evaluate(2), 0.001);
        assertEquals(4.0, mp.evaluateDerivative(2), 0.001);
    }

    @Test
    public void testPrintAOSandRPN() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x + 1", MathPlot.ExpressionFormat.AOS);

        List<String> aos = mp.print(MathPlot.ExpressionFormat.AOS);
        List<String> rpn = mp.print(MathPlot.ExpressionFormat.RPN);

        assertEquals(2, aos.size());
        assertEquals(2, rpn.size());
        assertTrue(aos.get(0).contains("f"));
        assertTrue(rpn.get(0).contains("f"));
    }

    /* ===================== PLOT STRATEGY ===================== */

    @Test
    public void testPlotStrategiesExist() {
        PlotStrategy cart = new CartesianPlotStrategy();
        PlotStrategy polar = new PolarPlotStrategy();

        assertNotNull(cart);
        assertNotNull(polar);
    }

    @Test
    public void testPlotDispatchDoesNotCrash() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas canvas = new Canvas(400, 400);

        assertDoesNotThrow(() ->
            mp.plot(canvas, MathPlot.PlotType.Cartesian)
        );

        assertDoesNotThrow(() ->
            mp.plot(canvas, MathPlot.PlotType.Polar)
        );
    }
}
