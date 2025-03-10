package io.github.stackphy.runtime;

import io.github.stackphy.model.StackItem;
import io.github.stackphy.model.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Environment for variable storage in StackPhy.
 */
public class Environment {
    private final Map<String, Variable> variables = new HashMap<>();
    
    /**
     * Defines a new variable in the environment.
     * 
     * @param name Variable name
     * @param value The stack item value
     * @param stochastic Whether this is a stochastic (random) variable
     * @return The created Variable
     * @throws IllegalArgumentException if a variable with this name already exists
     */
    public Variable defineVariable(String name, StackItem value, boolean stochastic) {
        if (variables.containsKey(name)) {
            throw new IllegalArgumentException("Variable '" + name + "' already defined");
        }
        
        Variable variable = new Variable(name, value, stochastic);
        variables.put(name, variable);
        return variable;
    }
    
    /**
     * Gets a variable by name.
     * 
     * @param name The variable name
     * @return The variable
     * @throws IllegalArgumentException if the variable doesn't exist
     */
    public Variable getVariable(String name) {
        Variable variable = variables.get(name);
        if (variable == null) {
            throw new IllegalArgumentException("Variable '" + name + "' not defined");
        }
        return variable;
    }
    
    /**
     * Checks if a variable exists.
     * 
     * @param name The variable name
     * @return true if it exists, false otherwise
     */
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
    
    /**
     * Gets all variable names in the environment.
     * 
     * @return Set of variable names
     */
    public Set<String> getVariableNames() {
        return variables.keySet();
    }
    
    /**
     * Gets all variables in the environment.
     * 
     * @return Map of variable names to variables
     */
    public Map<String, Variable> getVariables() {
        return new HashMap<>(variables);
    }
    
    /**
     * Gets all stochastic variables in the environment.
     * 
     * @return Map of variable names to stochastic variables
     */
    public Map<String, Variable> getStochasticVariables() {
        Map<String, Variable> stochasticVars = new HashMap<>();
        
        for (Map.Entry<String, Variable> entry : variables.entrySet()) {
            if (entry.getValue().isStochastic()) {
                stochasticVars.put(entry.getKey(), entry.getValue());
            }
        }
        
        return stochasticVars;
    }
    
    /**
     * Gets all deterministic variables in the environment.
     * 
     * @return Map of variable names to deterministic variables
     */
    public Map<String, Variable> getDeterministicVariables() {
        Map<String, Variable> deterministicVars = new HashMap<>();
        
        for (Map.Entry<String, Variable> entry : variables.entrySet()) {
            if (!entry.getValue().isStochastic()) {
                deterministicVars.put(entry.getKey(), entry.getValue());
            }
        }
        
        return deterministicVars;
    }
    
    /**
     * Clears all variables from the environment.
     */
    public void clear() {
        variables.clear();
    }
}
