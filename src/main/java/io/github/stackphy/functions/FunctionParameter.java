package io.github.stackphy.functions;

import io.github.stackphy.types.PhyloSpecType;
import java.util.Objects;

/**
 * Represents a parameter in a PhyloSpec function signature.
 */
public class FunctionParameter {
    private final String name;
    private final PhyloSpecType type;
    private final String defaultValue;
    
    /**
     * Creates a new function parameter.
     * 
     * @param name The parameter name
     * @param type The parameter type
     * @param defaultValue The default value (null if required)
     */
    public FunctionParameter(String name, PhyloSpecType type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }
    
    /**
     * Gets the parameter name.
     * 
     * @return The parameter name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the parameter type.
     * 
     * @return The parameter type
     */
    public PhyloSpecType getType() {
        return type;
    }
    
    /**
     * Gets the default value.
     * 
     * @return The default value, or null if required
     */
    public String getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Checks if the parameter is required.
     * 
     * @return true if required, false if optional
     */
    public boolean isRequired() {
        return defaultValue == null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionParameter that = (FunctionParameter) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(type, that.type) &&
               Objects.equals(defaultValue, that.defaultValue);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, type, defaultValue);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(": ").append(type.getTypeName());
        
        if (defaultValue != null) {
            builder.append(" = ").append(defaultValue);
        }
        
        return builder.toString();
    }
}