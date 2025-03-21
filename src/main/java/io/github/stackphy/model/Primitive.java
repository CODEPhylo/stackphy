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
