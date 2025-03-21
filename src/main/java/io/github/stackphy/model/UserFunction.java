package io.github.stackphy.model;

import io.github.stackphy.parser.Operation;
import java.util.List;

/**
 * Represents a user-defined function in StackPhy.
 */
public class UserFunction implements StackItem {
    private final String name;
    private final List<Operation> operations;
    private final String stackComment; // Optional stack comment for documentation
    
    /**
     * Creates a new user-defined function.
     * 
     * @param name The function name
     * @param operations The list of operations that make up the function body
     * @param stackComment Optional stack comment for documentation (can be null)
     */
    public UserFunction(String name, List<Operation> operations, String stackComment) {
        this.name = name;
        this.operations = operations;
        this.stackComment = stackComment;
    }
    
    /**
     * Gets the function name.
     * 
     * @return The function name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the operations that make up the function body.
     * 
     * @return The list of operations
     */
    public List<Operation> getOperations() {
        return operations;
    }
    
    /**
     * Gets the stack comment for documentation.
     * 
     * @return The stack comment, or null if not provided
     */
    public String getStackComment() {
        return stackComment;
    }
    
    @Override
    public StackItemType getType() {
        return StackItemType.FUNCTION;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(": ").append(name);
        
        if (stackComment != null && !stackComment.isEmpty()) {
            builder.append(" ").append(stackComment);
        }
        
        builder.append(" ... ;");
        return builder.toString();
    }
}