package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Operation that pushes a value onto the stack.
 */
public class ValueOperation extends AbstractOperation {
    private final Object value;
    
    /**
     * Creates a new value operation.
     * 
     * @param value The value to push
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public ValueOperation(Object value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    // Constructor for Integer values
    public ValueOperation(int value, int line, int column) {
        super(line, column);
        this.value = value;
    }
    
    // Constructor for Double values
    public ValueOperation(double value, int line, int column) {
        super(line, column);
        this.value = value;
    }
    
    // Make sure execute method preserves the type
    @Override
    public void execute(Stack stack, Environment env) {
        // Create the appropriate Primitive based on value type
        if (value instanceof Integer) {
            stack.push(new Primitive((Integer) value));
        } else if (value instanceof Double) {
            stack.push(new Primitive((Double) value));
        } else if (value instanceof String) {
            stack.push(new Primitive((String) value));
        } else {
            stack.push(new Primitive(value));
        }
    }    
    
    /**
     * Gets the value.
     * 
     * @return The value
     */
    public Object getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        System.out.println("toString called on ValueOperation with value: " + value + 
                   " (Type: " + (value != null ? value.getClass().getName() : "null") + ")");
        return "Value(" + value + ")";
    }
}
