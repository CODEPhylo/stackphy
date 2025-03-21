package io.github.stackphy;

import io.github.stackphy.model.*;
import io.github.stackphy.parser.*;
import io.github.stackphy.runtime.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Main parser class for StackPhy language with PhyloSpec semantic compatibility.
 * Provides both instance-based and static utility interfaces.
 */
public class StackPhyParser {
    private final Interpreter interpreter;
    
    /**
     * Creates a new StackPhy parser with a new interpreter.
     */
    public StackPhyParser() {
        this.interpreter = new Interpreter();
    }
    
    /**
     * Creates a new StackPhy parser with the given interpreter.
     * 
     * @param interpreter The interpreter to use
     */
    public StackPhyParser(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    /**
     * Parses a StackPhy file and builds the model.
     * 
     * @param file The StackPhy file to parse
     * @return The constructed Environment with the complete model
     * @throws IOException If the file cannot be read
     * @throws StackPhyException If there is a syntax or semantic error
     */
    public Environment parseFile(File file) throws IOException, StackPhyException {
        String content = Files.readString(file.toPath());
        return parseString(content);
    }
    
    /**
     * Parses StackPhy code from a string and builds the model.
     * 
     * @param code The StackPhy code to parse
     * @return The constructed Environment with the complete model
     * @throws StackPhyException If there is a syntax or semantic error
     */
    public Environment parseString(String code) throws StackPhyException {
        return parseString(code, true);
    }
    
    /**
     * Parses StackPhy code into operations without executing them.
     * 
     * @param code The StackPhy code to parse
     * @return The list of operations
     * @throws StackPhyException if an error occurs during parsing
     */
    public static List<Operation> parse(String code) throws StackPhyException {
        return ParserFactory.parse(code);
    }
    
    /**
     * Parses StackPhy code from a string and builds the model.
     * 
     * @param code The StackPhy code to parse
     * @param debug Whether to print debug information
     * @return The constructed Environment with the complete model
     * @throws StackPhyException If there is a syntax or semantic error
     */
    public Environment parseString(String code, boolean debug) throws StackPhyException {
        // Parse using the ParserFactory
        List<Operation> operations = ParserFactory.parse(code);
        
        if (debug) {
            // Print operations for debugging
            System.out.println("\nOperations:");
            for (Operation op : operations) {
                System.out.println("  " + op);
            }
            
            // Show execution
            System.out.println("\nExecution:");
            System.out.println("  Initial stack: " + interpreter.getStack());
        }
        
        // Execute all operations at once for proper function handling
        interpreter.execute(operations);
        
        if (debug) {
            // Show final stack state
            System.out.println("  Final stack: " + interpreter.getStack());
        }
        
        return interpreter.getEnvironment();
    }
    
    /**
     * Gets the interpreter.
     * 
     * @return The interpreter
     */
    public Interpreter getInterpreter() {
        return interpreter;
    }
    
    /**
     * Gets the stack from the interpreter.
     * 
     * @return The stack
     */
    public Stack getStack() {
        return interpreter.getStack();
    }
    
    /**
     * Gets the environment from the interpreter.
     * 
     * @return The environment
     */
    public Environment getEnvironment() {
        return interpreter.getEnvironment();
    }
    
    /**
     * Clears the interpreter's stack and environment.
     */
    public void clear() {
        interpreter.clear();
    }
    
    // Static convenience methods from PhyloSpecAdapter
    
    /**
     * Parses and executes PhyloSpec-compatible StackPhy code.
     * 
     * @param code The StackPhy code to parse and execute
     * @return The environment after execution
     * @throws StackPhyException if an error occurs during parsing or execution
     */
    public static Environment parseAndExecute(String code) throws StackPhyException {
        StackPhyParser parser = new StackPhyParser();
        return parser.parseString(code, false);
    }
    
    /**
     * Executes a list of operations.
     * 
     * @param operations The operations to execute
     * @return The environment after execution
     * @throws StackPhyException if an error occurs during execution
     */
    public static Environment execute(List<Operation> operations) throws StackPhyException {
        // Execute the operations
        Stack stack = new Stack();
        Environment env = new Environment();
        Interpreter interpreter = new Interpreter(stack, env);
        
        interpreter.execute(operations);
        
        return env;
    }
}