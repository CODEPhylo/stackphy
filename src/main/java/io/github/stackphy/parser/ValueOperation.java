package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Operation that pushes a value onto the stack.
 */
public class ValueOperation implements Operation {
    private final Object value;
    private final int line;
    private final int column;
    
    /**
     * Creates a new value operation.
     * 
     * @param value The value to push
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public ValueOperation(Object value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        try {
            stack.push(new Primitive(value));
        } catch (IllegalArgumentException e) {
            throw new StackPhyException("Invalid value: " + e.getMessage(), e, line, column);
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
        return String.format("Value(%s)", value);
    }
}
