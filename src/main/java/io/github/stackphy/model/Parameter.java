package io.github.stackphy.model;

/**
 * Interface for all parameters in the model.
 * Parameters are nodes in the model graph.
 */
public interface Parameter extends StackItem {
    /**
     * Gets the name of this parameter.
     * @return The parameter name, or null if this is an anonymous parameter
     */
    String getName();
    
    /**
     * Returns true if this parameter is anonymous (has no name).
     * @return true if anonymous, false otherwise
     */
    default boolean isAnonymous() {
        return getName() == null;
    }
    
    /**
     * Gets the value of this parameter.
     * For primitives, this is the underlying value.
     * For variables, this depends on whether they're deterministic or stochastic.
     * 
     * @return The value
     */
    Object getValue();
    
    /**
     * Gets the value as a double, if possible.
     * 
     * @return The numeric value as a double
     * @throws UnsupportedOperationException if the value cannot be converted to a double
     */
    default double getDoubleValue() {
        Object value = getValue();
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new UnsupportedOperationException("Parameter value cannot be converted to double");
    }
    
    /**
     * Returns whether this parameter's value can be represented as a double.
     * 
     * @return true if the value can be represented as a double, false otherwise
     */
    default boolean isNumeric() {
        return getValue() instanceof Number;
    }
    
    /**
     * Gets the value as a string, if possible.
     * 
     * @return The string value
     * @throws UnsupportedOperationException if the value cannot be converted to a string
     */
    default String getStringValue() {
        Object value = getValue();
        if (value instanceof String) {
            return (String) value;
        }
        throw new UnsupportedOperationException("Parameter value cannot be converted to string");
    }
    
    /**
     * Returns whether this parameter's value can be represented as a string.
     * 
     * @return true if the value can be represented as a string, false otherwise
     */
    default boolean isString() {
        return getValue() instanceof String;
    }
    
    /**
     * Gets the value as an array, if possible.
     * 
     * @return The array value
     * @throws UnsupportedOperationException if the value cannot be converted to an array
     */
    default Object[] getArrayValue() {
        Object value = getValue();
        if (value instanceof Object[]) {
            return (Object[]) value;
        }
        throw new UnsupportedOperationException("Parameter value cannot be converted to array");
    }
    
    /**
     * Returns whether this parameter's value can be represented as an array.
     * 
     * @return true if the value can be represented as an array, false otherwise
     */
    default boolean isArray() {
        return getValue() instanceof Object[];
    }
    
    @Override
    default StackItemType getType() {
        return StackItemType.PARAMETER;
    }
}
