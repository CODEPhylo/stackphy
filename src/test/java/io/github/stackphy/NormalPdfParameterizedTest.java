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
                         "  swap -\n" +
                         "  swap /\n" +
                         "  dup *\n" +
                         "  2 /\n" +
                         "  negate\n" +
                         "  exp\n" +
                         "  2 pi * sqrt\n" +
                         "  1 swap /\n" +
                         "  *\n" +
                         ";\n" +
                         sigma + " " + mu + " " + x + " normalPdf";
        
        List<Operation> operations = ParserFactory.parse(program);
        interpreter.execute(operations);
        
        Stack stack = interpreter.getStack();
        assertEquals(1, stack.size());

        assertEquals(expected, ((Parameter)stack.peek()).getDoubleValue(), 0.0001);
    }
}