package io.github.stackphy;

import io.github.stackphy.model.Parameter;
import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.ParserFactory;
import io.github.stackphy.parser.StackPhyException;
import io.github.stackphy.runtime.Interpreter;
import io.github.stackphy.runtime.Stack;

import java.util.List;
import java.util.Scanner;

/**
 * A simple REPL (Read-Eval-Print-Loop) for testing StackPhy expressions.
 * Allows interactive testing of StackPhy operations and inspecting the stack after each command.
 */
public class StackPhyRepl {
    
    private Interpreter interpreter;
    private final boolean debugMode;
    
    /**
     * Creates a new StackPhy REPL.
     * 
     * @param debugMode Whether to show detailed debugging information
     */
    public StackPhyRepl(boolean debugMode) {
        this.interpreter = new Interpreter(debugMode);
        this.debugMode = debugMode;
        ParserFactory.setDefaultType(ParserFactory.ParserType.ANTLR);
    }
    
    /**
     * Starts the REPL.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("StackPhy REPL - Interactive Mode");
        System.out.println("- Enter StackPhy expressions to execute them");
        System.out.println("- Type :help for commands");
        System.out.println("- Type :quit to exit");
        System.out.println();
        printStack();
        
        boolean running = true;
        
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            if (input.startsWith(":")) {
                // Command mode
                running = processCommand(input);
            } else {
                // StackPhy expression mode
                processExpression(input);
            }
        }
        
        scanner.close();
    }
    
    /**
     * Processes a REPL command.
     * 
     * @param command The command to process
     * @return false if the REPL should exit, true otherwise
     */
    private boolean processCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : "";
        
        switch (cmd) {
            case ":quit":
            case ":exit":
            case ":q":
                System.out.println("Exiting StackPhy REPL.");
                return false;
                
            case ":clear":
            case ":reset":
                interpreter = new Interpreter(debugMode);
                System.out.println("Stack and environment cleared.");
                printStack();
                break;
                
            case ":help":
                printHelp();
                break;
                
            case ":stack":
                printStack();
                break;
                
            case ":test":
                // Test a predefined function
                if (arg.equals("normalPdf")) {
                    testNormalPdf();
                } else {
                    System.out.println("Unknown test: " + arg);
                    System.out.println("Available tests: normalPdf");
                }
                break;
                
            default:
                System.out.println("Unknown command: " + cmd);
                System.out.println("Type :help for available commands.");
                break;
        }
        
        return true;
    }
    
    /**
     * Processes a StackPhy expression.
     * 
     * @param expression The expression to process
     */
    private void processExpression(String expression) {
        try {
            List<Operation> operations = ParserFactory.parse(expression);
            interpreter.execute(operations);
            printStack();
        } catch (StackPhyException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Prints the current stack state.
     */
    private void printStack() {
        Stack stack = interpreter.getStack();
        System.out.println("Stack: " + stack);
    }
    
    /**
     * Prints help information.
     */
    private void printHelp() {
        System.out.println("StackPhy REPL Commands:");
        System.out.println("  :help             - Show this help message");
        System.out.println("  :quit, :exit, :q  - Exit the REPL");
        System.out.println("  :clear, :reset    - Clear the stack and environment");
        System.out.println("  :stack            - Show the current stack");
        System.out.println("  :test normalPdf   - Test the normal PDF function");
        System.out.println();
        System.out.println("StackPhy expressions will be evaluated immediately.");
        System.out.println("The stack will be displayed after each evaluation.");
    }
    
    /**
     * Tests the normal PDF function with various inputs and shows the results.
     */
    private void testNormalPdf() {
        // Define the normalPdf function
        String normalPdfDef = ": normalPdf ( sigma mu x -- pdf )\n" +
                              "  -rot         \n" +
                              "  dup         \n" +
                              "  -rot        \n" +
                              "  rot         \n" +
                              "  swap -      \n" +
                              "  swap dup *  \n" +
                              "  2 *         \n" +
                              "  rot dup *   \n" +
                              "  rot /       \n" +
                              "  negate      \n" +
                              "  exp         \n" +
                              "  swap        \n" +
                              "  2 pi * sqrt *\n" +
                              "  1 swap /    \n" +
                              "  *           \n" +
                              ";";
        
        try {
            // Define the function
            List<Operation> operations = ParserFactory.parse(normalPdfDef);
            interpreter.execute(operations);
            System.out.println("Normal PDF function defined.");
            printStack();
            
            // Test cases from the ParameterizedTest
            double[][] testCases = {
                // sigma, mu, x, expected
                { 1.0, 0.0, 0.0, 0.3989 },
                { 1.0, -1.0, 0.0, 0.2420 },
                { 2.0, 0.0, 0.0, 0.1994 }, // This case was failing
                { 1.0, 0.0, 1.0, 0.2420 }
            };
            
            for (double[] testCase : testCases) {
                double sigma = testCase[0];
                double mu = testCase[1];
                double x = testCase[2];
                double expected = testCase[3];
                
                // Clear stack
                interpreter = new Interpreter(debugMode);
                
                // Define function again for the fresh interpreter
                interpreter.execute(operations);
                
                // Execute test case
                String testExpr = sigma + " " + mu + " " + x + " normalPdf";
                List<Operation> testOps = ParserFactory.parse(testExpr);
                interpreter.execute(testOps);
                
                // Get result
                Stack stack = interpreter.getStack();
                double result = ((Parameter) stack.peek()).getDoubleValue();
                
                System.out.printf("normalPdf(%.1f, %.1f, %.1f) = %.4f (expected: %.4f)%n",
                        sigma, mu, x, result, expected);
                System.out.println("Stack: " + stack);
                System.out.println();
            }
            
        } catch (StackPhyException e) {
            System.err.println("Error testing normalPdf: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error testing normalPdf: " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Main entry point.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        boolean debugMode = false;
        
        // Check for debug flag
        for (String arg : args) {
            if (arg.equals("--debug")) {
                debugMode = true;
                break;
            }
        }
        
        StackPhyRepl repl = new StackPhyRepl(debugMode);
        repl.start();
    }
}