package io.github.stackphy;

import io.github.stackphy.parser.Lexer;
import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.Parser;
import io.github.stackphy.parser.StackPhyException;
import io.github.stackphy.parser.Token;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Interpreter;
import io.github.stackphy.runtime.Stack;

import java.util.List;

/**
 * Adapter for StackPhy parsing and execution that is semantically compatible with PhyloSpec.
 * Provides a clean interface for the StackPhy parser.
 */
public class PhyloSpecAdapter {
    
    /**
     * Parses and executes PhyloSpec-compatible StackPhy code.
     * 
     * @param code The StackPhy code to parse and execute
     * @return The environment after execution
     * @throws StackPhyException if an error occurs during parsing or execution
     */
    public static Environment parseAndExecute(String code) throws StackPhyException {
        // Create lexer and tokenize the code
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        
        // Parse the tokens using the updated Parser
        Parser parser = new Parser(tokens);
        List<Operation> operations = parser.parse();
        
        // Execute the operations
        Stack stack = new Stack();
        Environment env = new Environment();
        Interpreter interpreter = new Interpreter(stack, env);
        
        for (Operation operation : operations) {
            interpreter.execute(operation);
        }
        
        return env;
    }
    
    /**
     * Parses StackPhy code into operations without executing them.
     * 
     * @param code The StackPhy code to parse
     * @return The list of operations
     * @throws StackPhyException if an error occurs during parsing
     */
    public static List<Operation> parse(String code) throws StackPhyException {
        // Create lexer and tokenize the code
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        
        // Parse the tokens using the StackPhy parser
        Parser parser = new Parser(tokens);
        return parser.parse();
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
        
        for (Operation operation : operations) {
            interpreter.execute(operation);
        }
        
        return env;
    }
}