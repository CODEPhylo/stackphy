package io.github.stackphy;

import io.github.stackphy.model.*;
import io.github.stackphy.parser.*;
import io.github.stackphy.runtime.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Main parser class for StackPhy language.
 * Coordinates lexing, parsing, and model building.
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
    // Create lexer and generate tokens
    Lexer lexer = new Lexer(code);
    List<Token> tokens = lexer.tokenize();
    
    // Print tokens for debugging
    System.out.println("Tokens:");
    for (Token token : tokens) {
        System.out.println("  " + token);
    }
    
    // Create parser and parse tokens
    Parser parser = new Parser(tokens);
    List<Operation> operations = parser.parse();
    
    // Print operations for debugging
    System.out.println("\nOperations:");
    for (Operation op : operations) {
        System.out.println("  " + op);
    }
    
    // Execute operations using the interpreter with more debugging
    System.out.println("\nExecution:");
    for (Operation op : operations) {
        System.out.println("  Before " + op + ": Stack = " + interpreter.getStack());
        interpreter.execute(op);
        System.out.println("  After  " + op + ": Stack = " + interpreter.getStack());
        System.out.println();
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
}