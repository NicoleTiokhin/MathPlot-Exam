package MathPlot.Parsers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Stack;

public class RPNTest {

    @Test
    void testSimpleAdd() throws Exception {
        RPN r = new RPN("3 4 +");
        Stack<String> s = r.parse();
        assertEquals("+", s.pop());
        assertEquals("4", s.pop());
        assertEquals("3", s.pop());
    }

    @Test
    void testUnarySin() throws Exception {
        RPN r = new RPN("x sin");
        Stack<String> s = r.parse();
        assertEquals("sin", s.pop());
        assertEquals("x", s.pop());
    }

    @Test
    void testWhitespaceHandling() throws Exception {
        RPN r = new RPN("   2   3   *   ");
        Stack<String> s = r.parse();
        assertEquals("*", s.pop());
    }

    @Test
void testLnIsRejected() {
    RPN r = new RPN("x ln");
    assertThrows(Exception.class, r::parse);
}


    // NO test for "log", because your RPN parser rejects it.

    @Test
    void testInvalidToken() {
        RPN r = new RPN("x ?");
        assertThrows(Exception.class, r::parse);
    }

    @Test
    void testTooFewOperandsForOp() {
        RPN r = new RPN("+");
        assertThrows(Exception.class, r::parse);
    }

    @Test
    void testLeftoverStack() {
        RPN r = new RPN("2 3");
        assertThrows(Exception.class, r::parse);
    }

    @Test
    void testUnaryMissingArgument() {
        RPN r = new RPN("sin");
        assertThrows(Exception.class, r::parse);
    }

    @Test
    void testGarbageInput() {
        RPN r = new RPN("#$%");
        assertThrows(Exception.class, r::parse);
    }

    @Test
    void testEmptyInput() {
        RPN r = new RPN("   ");
        assertThrows(Exception.class, r::parse);
    }
}
