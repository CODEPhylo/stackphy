package io.github.stackphy.runtime;

import io.github.stackphy.model.UserFunction;
import io.github.stackphy.parser.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interpreter for executing StackPhy programs.
 */
public class Interpreter {
    private final Stack stack;
    private final Environment env;
    private boolean debugMode = false;

    public Interpreter() {
        this(new Stack(), new Environment(), false);
    }
    
    // Add constructors with debug mode
    public Interpreter(boolean debugMode) {
        this(new Stack(), new Environment(), debugMode);
    }
    
    public Interpreter(Stack stack, Environment environment) {
        this(stack,environment, false);
    }

    public Interpreter(Stack stack, Environment environment, boolean debugMode) {
        this.stack = stack;
        this.env = environment;
        this.debugMode = debugMode;
    }
    
    // Add getters/setters for debug mode
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    // Modify execute method to print stack after each operation
    public void execute(List<Operation> operations) throws StackPhyException {
        int i = 0;
        while (i < operations.size()) {
            Operation op = operations.get(i);
            
            if (debugMode) {
                System.out.println("Before executing: " + op);
                System.out.println("Stack: " + stack);
            }
            
            // Special handling for function definition
            if (op instanceof FunctionStartOperation) {
                // We need to collect the function definition
                i = handleFunctionDefinition(operations, i);
            } else {
                // Regular operation
                op.execute(stack, env);
                
                if (debugMode) {
                    System.out.println("After executing: " + op);
                    System.out.println("Stack: " + stack);
                    System.out.println("--------------------------");
                }
                
                i++;
            }
        }
    }

    
    /**
     * Handles a function definition.
     * 
     * @param operations The list of operations
     * @param startIndex The index of the function start operation
     * @return The index after the function definition
     * @throws StackPhyException if the function definition is invalid
     */
    private int handleFunctionDefinition(List<Operation> operations, int startIndex) throws StackPhyException {
        int i = startIndex + 1;
        
        // Expect function name
        if (i >= operations.size() || !(operations.get(i) instanceof FunctionNameOperation)) {
            throw new StackPhyException("Expected function name after ':'", 
                    operations.get(startIndex).getLine(), operations.get(startIndex).getColumn());
        }
        
        FunctionNameOperation nameOp = (FunctionNameOperation) operations.get(i);
        String functionName = nameOp.getName();
        i++;
        
        // Optional stack comment
        String stackComment = null;
        if (i < operations.size() && operations.get(i) instanceof StackCommentOperation) {
            StackCommentOperation commentOp = (StackCommentOperation) operations.get(i);
            stackComment = commentOp.getComment();
            i++;
        }
        
        // Collect function body until we find a function end operation (;)
        List<Operation> functionBody = new ArrayList<>();
        while (i < operations.size() && !(operations.get(i) instanceof FunctionEndOperation)) {
            functionBody.add(operations.get(i));
            i++;
        }
        
        // Verify we found the function end
        if (i >= operations.size()) {
            throw new StackPhyException("Unterminated function definition: missing ';'", 
                    nameOp.getLine(), nameOp.getColumn());
        }
        
        // Skip the function end operation
        i++;
        
        // Create and register the function
        UserFunction function = new UserFunction(functionName, functionBody, stackComment);
        env.defineFunction(function);
        
        return i;
    }
    
    /**
     * Gets the stack.
     * 
     * @return The stack
     */
    public Stack getStack() {
        return stack;
    }
    
    /**
     * Gets the environment.
     * 
     * @return The environment
     */
    public Environment getEnvironment() {
        return env;
    }
    
    /**
     * Clears the stack and environment.
     */
    public void clear() {
        stack.clear();
        env.clear();
    }
}