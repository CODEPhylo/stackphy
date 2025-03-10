/*
 * Variable.java
 */
package io.github.stackphy.model;

/**
 * Represents a named variable in the model.
 * A variable can be either stochastic (random) or deterministic.
 */
public class Variable implements Parameter {
    private final String name;
    private final StackItem value;
    private final boolean stochastic;
    private StackItem observedData; // For stochastic variables
    
    /**
     * Creates a new variable.
     * 
     * @param name The variable name
     * @param value The value (distribution for stochastic, parameter for deterministic)
     * @param stochastic Whether this is a stochastic (random) variable
     */
    public Variable(String name, StackItem value, boolean stochastic) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Variable value cannot be null");
        }
        
        // Validate that stochastic variables have a distribution
        if (stochastic && value.getType() != StackItemType.DISTRIBUTION) {
            throw new IllegalArgumentException("Stochastic variables must have a distribution");
        }
        
        this.name = name;
        this.value = value;
        this.stochastic = stochastic;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Gets the current value of this variable.
     * For deterministic variables, this returns the underlying value.
     * For stochastic variables, this generates or returns a value from the distribution.
     * 
     * @return The current value
     */
    @Override
    public Object getValue() {
        if (stochastic) {
            // For stochastic variables, get value from distribution
            if (observedData != null) {
                // If observed, return observed data
                return observedData;
            } else {
                // Otherwise, generate from distribution
                Distribution dist = (Distribution) value;
                // Either cache and return a generated value, or generate on demand
                // For now, generate on demand
                return dist.generateValue().getValue();
            }
        } else {
            // For deterministic variables, return the underlying value
            if (value instanceof Parameter) {
                return ((Parameter) value).getValue();
            }
            return value;
        }
    }
        
    /**
     * Gets the underlying stack item for this variable.
     * For stochastic variables, this is the distribution.
     * For deterministic variables, this can be any stack item.
     * 
     * @return The underlying stack item
     */
    public StackItem getUnderlyingValue() {
        return value;
    }
    
    /**
     * Returns whether this is a stochastic (random) variable.
     * 
     * @return true if stochastic, false if deterministic
     */
    public boolean isStochastic() {
        return stochastic;
    }
    
    /**
     * Gets the distribution for this variable.
     * Only valid for stochastic variables.
     * 
     * @return The distribution
     * @throws IllegalStateException if this is not a stochastic variable
     */
    public Distribution getDistribution() {
        if (!stochastic) {
            throw new IllegalStateException("Not a stochastic variable");
        }
        return (Distribution) value;
    }
    
    /**
     * Sets observed data for this variable.
     * Only valid for stochastic variables.
     * 
     * @param data The observed data
     * @throws IllegalStateException if this is not a stochastic variable
     */
    public void setObservedData(StackItem data) {
        if (!stochastic) {
            throw new IllegalStateException("Cannot set observed data for deterministic variable");
        }
        this.observedData = data;
    }
    
    /**
     * Gets the observed data for this variable.
     * Only valid for stochastic variables.
     * 
     * @return The observed data, or null if no data has been observed
     * @throws IllegalStateException if this is not a stochastic variable
     */
    public StackItem getObservedData() {
        if (!stochastic) {
            throw new IllegalStateException("Deterministic variables do not have observed data");
        }
        return observedData;
    }
    
    /**
     * Returns whether this variable has observed data.
     * Only valid for stochastic variables.
     * 
     * @return true if this variable has observed data, false otherwise
     * @throws IllegalStateException if this is not a stochastic variable
     */
    public boolean hasObservedData() {
        if (!stochastic) {
            throw new IllegalStateException("Deterministic variables do not have observed data");
        }
        return observedData != null;
    }
    
    @Override
    public double getDoubleValue() {
        if (stochastic) {
            Distribution dist = (Distribution) value;
            return dist.generateValue().getDoubleValue();
        } else if (value instanceof Parameter) {
            return ((Parameter) value).getDoubleValue();
        }
        Object val = getValue();
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        throw new UnsupportedOperationException("Variable value cannot be converted to double");
    }
    
    @Override
    public boolean isArray() {
        if (stochastic) {
            // For stochastic variables, delegate to the distribution's generateValue
            Distribution dist = (Distribution) value;
            return dist.generateValue().isArray();
        } else if (value instanceof Parameter) {
            return ((Parameter) value).isArray();
        }
        return getValue() instanceof Object[];
    }
    
    @Override
    public Object[] getArrayValue() {
        if (stochastic) {
            // For stochastic variables, delegate to the distribution's generateValue
            Distribution dist = (Distribution) value;
            return dist.generateValue().getArrayValue();
        } else if (value instanceof Parameter) {
            return ((Parameter) value).getArrayValue();
        }
        Object val = getValue();
        if (val instanceof Object[]) {
            return (Object[]) val;
        }
        throw new UnsupportedOperationException("Variable value cannot be converted to array");
    }
}
