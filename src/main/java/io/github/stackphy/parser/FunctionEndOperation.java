package io.github.stackphy.parser;

import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Operation that ends a function definition.
 */
public class FunctionEndOperation extends AbstractOperation {
    
    /**
     * Creates a new function end operation.
     * 
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public FunctionEndOperation(int line, int column) {
        super(line, column);
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        // Function end is handled specially by the interpreter
        // This should not be executed directly
        throw new StackPhyException("Function end operation should not be executed directly", getLine(), getColumn());
    }
    
    @Override
    public String toString() {
        return "FunctionEnd";
    }
}