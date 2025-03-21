package io.github.stackphy.parser;

import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Operation that defines a function name.
 */
public class FunctionNameOperation extends AbstractOperation {
    private final String name;
    
    /**
     * Creates a new function name operation.
     * 
     * @param name The function name
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public FunctionNameOperation(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        // Function name is handled specially by the interpreter
        // This should not be executed directly
        throw new StackPhyException("Function name operation should not be executed directly", getLine(), getColumn());
    }
    
    /**
     * Gets the function name.
     * 
     * @return The function name
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("FunctionName(%s)", name);
    }
}