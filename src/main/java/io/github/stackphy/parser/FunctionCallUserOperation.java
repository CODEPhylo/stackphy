package io.github.stackphy.parser;

import io.github.stackphy.model.UserFunction;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.List;

/**
 * Operation that executes a user-defined function.
 */
public class FunctionCallUserOperation extends AbstractOperation {
    private final String functionName;
    
    /**
     * Creates a new user function call operation.
     * 
     * @param functionName The name of the function to call
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public FunctionCallUserOperation(String functionName, int line, int column) {
        super(line, column);
        this.functionName = functionName;
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        try {
            // Get the function from the environment
            UserFunction function = env.getFunction(functionName);
            
            if (function == null) {
                throw new StackPhyException("Undefined function: " + functionName, getLine(), getColumn());
            }
            
            // Create a new environment for function execution (for future scoping support)
            // For now, just use the current environment
            
            // Execute the function operations
            for (Operation op : function.getOperations()) {
                op.execute(stack, env);
            }
        } catch (StackPhyException e) {
            throw e; // Re-throw StackPhy exceptions as is
        } catch (Exception e) {
            throw new StackPhyException("Error executing function '" + functionName + "': " + e.getMessage(), 
                    e, getLine(), getColumn());
        }
    }
    
    @Override
    public String toString() {
        return String.format("FunctionCall(%s)", functionName);
    }
}