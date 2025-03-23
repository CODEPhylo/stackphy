package io.github.stackphy;

import static org.junit.Assert.*;

import io.github.stackphy.model.Parameter;
import org.junit.Before;
import org.junit.Test;
import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.ParserFactory;
import io.github.stackphy.runtime.Interpreter;
import io.github.stackphy.runtime.Stack;
import io.github.stackphy.model.StackItem;
import java.util.List;

public class StackPhyFunctionTest {
    
    private Interpreter interpreter;
    
    @Before
    public void setUp() {
        interpreter = new Interpreter(false);  // Set debug mode to false for tests
        ParserFactory.setDefaultType(ParserFactory.ParserType.ANTLR);
    }
    
    @Test
    public void testDoubleFunction() throws Exception {
        // Define the program
        String program = ": double ( n -- n*2 ) 2 * ; 5 double";
        
        // Parse and execute
        List<Operation> operations = ParserFactory.parse(program);
        interpreter.execute(operations);
        
        // Assert the result
        Stack stack = interpreter.getStack();
        assertEquals(1, stack.size());
        assertEquals(10.0, ((Parameter)stack.peek()).getDoubleValue(), 0.0001);
    }
    
    @Test
    public void testNormalPdfStandardCase() throws Exception {
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
                         "1 0 0 normalPdf";
        
        List<Operation> operations = ParserFactory.parse(program);
        interpreter.execute(operations);
        
        Stack stack = interpreter.getStack();
        assertEquals(1, stack.size());
        assertEquals(0.3989, ((Parameter)stack.peek()).getDoubleValue(), 0.0001);
    }
    
    @Test
    public void testNormalPdfOffsetMean() throws Exception {
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
                         "1 -1 0 normalPdf";
        
        List<Operation> operations = ParserFactory.parse(program);
        interpreter.execute(operations);
        
        Stack stack = interpreter.getStack();
        assertEquals(1, stack.size());
        assertEquals(0.2420, ((Parameter)stack.peek()).getDoubleValue(), 0.0001);
    }
    
    @Test
    public void testStandardNormal() throws Exception {
        String program = ": standardNormal ( -- pdf )\n" +
                         "  2 pi *\n" +
                         "  sqrt\n" +
                         "  1 swap /\n" +
                         ";\n" +
                         "standardNormal";
        
        List<Operation> operations = ParserFactory.parse(program);
        interpreter.execute(operations);
        
        Stack stack = interpreter.getStack();
        assertEquals(1, stack.size());
        assertEquals(0.3989, ((Parameter)stack.peek()).getDoubleValue(), 0.0001);
    }
}