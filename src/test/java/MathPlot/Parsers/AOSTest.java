package MathPlot.Parsers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AOSTest {

    @Test
    void testAddOperator() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("(x + 3)");
        assertEquals("+", p.main);
    }

    @Test
    void testSubtractOperator() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("(x - 5)");
        assertEquals("-", p.main);
    }

    @Test
    void testMultiplyOperator() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("(x * 2)");
        assertEquals("*", p.main);
    }

    @Test
    void testDivideOperator() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("(10 / x)");
        assertEquals("/", p.main);
    }

    @Test
    void testPowerOperator() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("(x ^ 4)");
        assertEquals("^", p.main);
    }

    @Test
    void testUnarySin() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("sin(x)");
        assertEquals("sin", p.main);
    }

    @Test
    void testUnaryNested() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("exp(sin(x))");
        assertEquals("exp", p.main);
    }

    @Test
    void testAtomNested() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("(((x)))");
        assertEquals("x", p.main);
    }

    @Test
    void testNumberAtom() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("42");
        assertEquals("42", p.main);
    }

    // IMPORTANT FIX: AOS picks first top-level split which is "+"
    @Test
    void testDeepBinaryExpression() throws Exception {
        AOS aos = new AOS();
        AOS.Parts p = aos.parse("((x+1)*(x-1))");
        assertEquals("+", p.main);
    }

    // Only malformed parentheses produce errors
    @Test
    void testMissingClosingParen() {
        AOS aos = new AOS();
        assertThrows(Exception.class, () -> aos.parse("(x + 1"));
    }

    @Test
    void testMissingOpeningParen() {
        AOS aos = new AOS();
        assertThrows(Exception.class, () -> aos.parse("x + 1)"));
    }
}
