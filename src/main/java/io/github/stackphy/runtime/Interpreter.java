package io.github.stackphy.runtime;

import io.github.stackphy.model.*;
import io.github.stackphy.parser.Operation;
import io.github.stackphy.parser.StackPhyException;

import java.util.List;

/**
 * Interpreter for the StackPhy language.
 * Executes operations on the stack and environment.
 */
public class Interpreter {
    private final Stack stack;
    private final Environment environment;
    
    /**
     * Creates a new interpreter with a new stack and environment.
     */
    public Interpreter() {
        this.stack = new Stack();
        this.environment = new Environment();
    }
    
    /**
     * Creates a new interpreter with the given stack and environment.
     * 
     * @param stack The stack to use
     * @param environment The environment to use
     */
    public Interpreter(Stack stack, Environment environment) {
        this.stack = stack;
        this.environment = environment;
    }
    
    /**
     * Executes a list of operations.
     * 
     * @param operations The operations to execute
     * @throws StackPhyException if an error occurs during execution
     */
    public void execute(List<Operation> operations) throws StackPhyException {
        for (Operation operation : operations) {
            execute(operation);
        }
    }
    
    /**
     * Executes a single operation.
     * 
     * @param operation The operation to execute
     * @throws StackPhyException if an error occurs during execution
     */
    public void execute(Operation operation) throws StackPhyException {
        operation.execute(stack, environment);
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
        return environment;
    }
    
    /**
     * Clears the stack and environment.
     */
    public void clear() {
        stack.clear();
        environment.clear();
    }
}