package io.github.stackphy;

import static org.junit.Assert.*;

import io.github.stackphy.model.Parameter;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.ParserFactory;
import io.github.stackphy.runtime.Interpreter;
import io.github.stackphy.runtime.Stack;
import java.util.List;


@RunWith(Parameterized.class)
public class NormalPdfParameterizedTest {
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // sigma, mu, x, expected result
            { 1.0, 0.0, 0.0, 0.3989 },
            { 1.0, -1.0, 0.0, 0.2420 },
            { 2.0, 0.0, 0.0, 0.1994 },
            { 1.0, 0.0, 1.0, 0.2420 }
        });
    }
    
    private double sigma;
    private double mu;
    private double x;
    private double expected;
    private Interpreter interpreter;
    
    public NormalPdfParameterizedTest(double sigma, double mu, double x, double expected) {
        this.sigma = sigma;
        this.mu = mu;
        this.x = x;
        this.expected = expected;
    }
    
    @Before
    public void setUp() {
        interpreter = new Interpreter(false);
        ParserFactory.setDefaultType(ParserFactory.ParserType.ANTLR);
    }
    
    @Test
    public void testNormalPdf() throws Exception {
        String program = ": normalPdf ( sigma mu x -- pdf )\n" +
                         "  swap -        // Calculate (x-mu)\n" +
                         "  swap dup rot  // Duplicate sigma for later\n" +
                         "  dup *         // Square (x-mu): (x-mu)²\n" +
                         "  swap dup *    // Square σ: σ²\n" +
                         "  2 *           // Multiply by 2: 2σ²\n" +
                         "  /             // Stack: sigma (x-mu)^2/(2*sigma^2)\n" +
                         "  negate        // Stack: sigma -(x-mu)^2/(2*sigma^2)\n" +
                         "  exp           // Stack: sigma e^(-(x-mu)^2/(2*sigma^2))\n" +
                         "  swap          // Stack: e^(...) sigma\n" +
                         "  2 pi *        // Stack: e^(...) sigma 2*pi\n" +
                         "  sqrt          // Stack: e^(...) sigma sqrt(2*pi)\n" +
                         "  *             // Stack: e^(...) sigma*sqrt(2*pi)\n" +
                         "  1 swap /      // Stack: e^(...) 1/(sigma*sqrt(2*pi))\n" +
                         "  *             // Final result\n" +
                         ";\n" +
                         sigma + " " + mu + " " + x + " normalPdf";
        
        List<Operation> operations = ParserFactory.parse(program);
        interpreter.execute(operations);
        
        Stack stack = interpreter.getStack();
        assertEquals(1, stack.size());

        assertEquals(expected, ((Parameter)stack.peek()).getDoubleValue(), 0.0001);
    }
}