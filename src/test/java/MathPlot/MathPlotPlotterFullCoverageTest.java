package MathPlot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MathPlotPlotterFullCoverageTest {

    private Canvas C() { return new Canvas(400, 400); }

    @Test
    void testPlotCartesianFull() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas c = C();
        assertDoesNotThrow(() -> mp.plot(c, MathPlot.PlotType.Cartesian));

        assertDoesNotThrow(() -> c.setWidth(600));
        assertDoesNotThrow(() -> c.setHeight(500));
    }

    @Test
    void testPlotPolarFull() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas c = C();
        assertDoesNotThrow(() -> mp.plot(c, MathPlot.PlotType.Polar));
    }

    @Test
    void testMousePressDrag() {
        MathPlot mp = new MathPlot();
        mp.setExpression("x", MathPlot.ExpressionFormat.AOS);

        Canvas c = C();
        mp.plot(c, MathPlot.PlotType.Cartesian);

        MouseEvent press = new MouseEvent(MouseEvent.MOUSE_PRESSED,
                100, 100, 100, 100, MouseButton.PRIMARY, 1,
                false,false,false,false,false,false,false,false,false,false,null);

        MouseEvent drag = new MouseEvent(MouseEvent.MOUSE_DRAGGED,
                200, 120, 200, 120, MouseButton.PRIMARY, 1,
                false,false,false,false,false,false,false,false,false,false,null);

        assertDoesNotThrow(() -> c.getOnMousePressed().handle(press));
        assertDoesNotThrow(() -> c.getOnMouseDragged().handle(drag));
    }

    

    @Test
    void testPointAndIteratorCoverage() {
        Point p = new Point(3,4);
        assertEquals(3, p.x());
        assertEquals(4, p.y());

        Point.Iterator dummy = new Point.Iterator() {
            int i = 0;
            @Override public void reset(){ i = 0; }
            @Override public boolean hasNext(){ return i < 1; }
            @Override public Point nextPoint(){ i++; return p; }
            @Override public boolean hasBreak(){ return false; }
        };

        dummy.reset();
        assertTrue(dummy.hasNext());
        assertEquals(p, dummy.nextPoint());
    }
}
