package io.github.stackphy;

import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.ParserFactory;
import io.github.stackphy.parser.StackPhyException;
import io.github.stackphy.runtime.Interpreter;
import io.github.stackphy.runtime.Stack;

import java.util.List;

/**
 * Simple test for function definition capability in StackPhy.
 */
public class FunctionDefinitionTest {
    
    public static void main(String[] args) {
        testSimpleFunction();
        System.out.println("\n============================================\n");
        testNormalPdfFunction();
    }
    
    /**
     * Tests a simple doubling function.
     */
    private static void testSimpleFunction() {
        try {
            // Define a simple test program with a function
            String program = ": double ( n -- n*2 ) 2 * ; 5 double // Puts 10 on the stack";
            
            // Print the test program
            System.out.println("Testing StackPhy function definition with:");
            System.out.println(program);
            System.out.println();
            
            // Set parser type to ANTLR
            ParserFactory.setDefaultType(ParserFactory.ParserType.ANTLR);
            
            // Parse directly using the factory
            System.out.println("Parsing with " + ParserFactory.getDefaultType() + " parser...");
            List<Operation> operations = ParserFactory.parse(program);
            
            // Print operations for debugging
            System.out.println("Operations:");
            for (Operation op : operations) {
                System.out.println("  " + op);
            }
            System.out.println();
            
            // Execute
            System.out.println("Executing...");
            Interpreter interpreter = new Interpreter();
            interpreter.execute(operations);
            
            // Print stack after execution
            Stack stack = interpreter.getStack();
            System.out.println("Final stack:");
            System.out.println(stack);
            
            // Success message if we got here
            System.out.println("\nSimple function definition test succeeded!");
            
        } catch (Exception e) {
            System.err.println("Error testing simple function definition:");
            e.printStackTrace();
        }
    }
    
    /**
     * Tests a more complex function for calculating normal PDF.
     */
    private static void testNormalPdfFunction() {
        try {
            // Define a more complex test program with a normal PDF function
            String program =
                ": normalPdf ( x mu sigma -- pdf )\n" +
                "  2 pick 2 pick -    // grabs x and mu, does (x - mu)\n" +
                "  dup *              // square it\n" +
                "  2 pick dup *       // picks sigma, squares it\n" +
                "  2.0 *              // multiply by 2 => 2*sigma^2\n" +
                "  / negate           // exponent denominator => -((x - mu)^2/(2 sigma^2))\n" +
                "  exp\n" +
                "  over 2 pick        // over gets sigma again on top, pick duplicates it\n" +
                "  2 pi * sqrt *\n" +
                "  /\n" +
                "  nip nip            // remove the leftover x, mu references if needed\n" +
                ";\n\n" +             
                "// Test with standard normal at x=0\n" +
                "0 0 1 normalPdf // Should give approximately 0.398942";             
                         
            // Print the test program
            System.out.println("Testing StackPhy with normal PDF function:");
            System.out.println(program);
            System.out.println();
            
            // Set parser type to ANTLR
            ParserFactory.setDefaultType(ParserFactory.ParserType.ANTLR);
            
            // Parse directly using the factory
            System.out.println("Parsing with " + ParserFactory.getDefaultType() + " parser...");
            List<Operation> operations = ParserFactory.parse(program);
            
            // Print operations for debugging
            System.out.println("Operations:");
            for (Operation op : operations) {
                System.out.println("  " + op);
            }
            System.out.println();
            
            // Execute
            System.out.println("Executing...");
            Interpreter interpreter = new Interpreter();
            interpreter.execute(operations);
            
            // Print stack after execution
            Stack stack = interpreter.getStack();
            System.out.println("Final stack:");
            System.out.println(stack);
            
            // Success message if we got here
            System.out.println("\nNormal PDF function test succeeded!");
            
        } catch (Exception e) {
            System.err.println("Error testing normal PDF function:");
            e.printStackTrace();
        }
    }
}