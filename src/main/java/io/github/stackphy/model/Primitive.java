/*
 * Primitive.java
 */
package io.github.stackphy.model;

/**
 * Implementation of an anonymous parameter (primitive value).
 * Used for literals (numbers, strings, arrays).
 */
public class Primitive implements Parameter {
    private final Object value;
    
    public Primitive(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        
        // Only allow number, string, or array
        if (!(value instanceof Number || value instanceof String || value instanceof Object[])) {
            throw new IllegalArgumentException("Value must be Number, String, or Array");
        }
        
        this.value = value;
    }
    
    // Constructor for Integer values
    public Primitive(int value) {
        this.value = value;
    }
    
    // Constructor for Double values
    public Primitive(double value) {
        this.value = value;
    }
    
    // Override the isInteger method from Parameter interface
    @Override
    public boolean isInteger() {
        return getValue() instanceof Integer;
    }
    
    // Override the isDouble method from Parameter interface
    @Override
    public boolean isDouble() {
        return getValue() instanceof Double;
    }

    // Make sure getDoubleValue handles both Integer and Double values
    @Override
    public double getDoubleValue() {
        Object val = getValue();
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        throw new UnsupportedOperationException("Primitive value cannot be converted to double");
    }
    
    @Override
    public String getName() {
        return null; // Anonymous parameter has no name
    }
    
    @Override
    public Object getValue() {
        return value;
    }
    
    public String toString() {
        return value.toString();
    }
}
