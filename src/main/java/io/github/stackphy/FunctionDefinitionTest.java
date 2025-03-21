package io.github.stackphy;
import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.ParserFactory;
import io.github.stackphy.runtime.Interpreter;
import io.github.stackphy.runtime.Stack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/**
 * Simple test for function definition capability in StackPhy that reads from example files.
 */
public class FunctionDefinitionTest {
    
    // Directory containing example StackPhy programs
    private static final String EXAMPLES_DIR = "examples";
    
    public static void main(String[] args) {
        if (args.length >= 2) {
            // If arguments provided, run the specified test
            runTest(args[0], args[1]);
        } else {
            // Default behavior
            runTest("simple_double.stackphy", "Simple doubling function");
            System.out.println("\n============================================\n");
            runTest("stack_debug.stackphy", "Stack debug");
        }
    }
    
    /**
     * Runs a test using a StackPhy program from a file.
     * 
     * @param filename The name of the file in the examples directory
     * @param description A short description of the test
     */
    private static void runTest(String filename, String description) {
        try {
            // Build the full path to the file
            Path filePath = Paths.get(EXAMPLES_DIR, filename);
            
            // Read the program from the file
            String program = readProgramFromFile(filePath);
            
            // Print the test program
            System.out.println("Testing StackPhy with " + description + ":");
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
            Interpreter interpreter = new Interpreter(true); // Enable debug mode
            interpreter.execute(operations);
            
            // Print stack after execution
            Stack stack = interpreter.getStack();
            System.out.println("Final stack:");
            System.out.println(stack);
            
            // Success message if we got here
            System.out.println("\n" + description + " test succeeded!");
            
        } catch (Exception e) {
            System.err.println("Error testing " + description + ":");
            e.printStackTrace();
        }
    }
    
    /**
     * Reads a StackPhy program from a file.
     * 
     * @param filePath The path to the file
     * @return The contents of the file as a string
     * @throws IOException If the file cannot be read
     */
    private static String readProgramFromFile(Path filePath) throws IOException {
        // Check if the file exists
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath);
        }
        
        // Read all lines from the file
        List<String> lines = Files.readAllLines(filePath);
        
        // Join the lines with newlines
        return String.join("\n", lines);
    }
}