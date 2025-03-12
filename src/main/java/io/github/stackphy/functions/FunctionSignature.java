package io.github.stackphy.functions;

import io.github.stackphy.types.PhyloSpecType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a PhyloSpec function signature.
 */
public class FunctionSignature {
    private final String name;
    private final List<FunctionParameter> parameters;
    private final PhyloSpecType returnType;
    
    /**
     * Creates a new function signature.
     * 
     * @param name The function name
     * @param parameters The function parameters
     * @param returnType The function return type
     */
    public FunctionSignature(String name, List<FunctionParameter> parameters, PhyloSpecType returnType) {
        this.name = name;
        this.parameters = Collections.unmodifiableList(parameters);
        this.returnType = returnType;
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
     * Gets the function parameters.
     * 
     * @return The function parameters
     */
    public List<FunctionParameter> getParameters() {
        return parameters;
    }
    
    /**
     * Gets the function return type.
     * 
     * @return The function return type
     */
    public PhyloSpecType getReturnType() {
        return returnType;
    }
    
    /**
     * Checks if the given arguments match the function parameters.
     * 
     * @param arguments The arguments to check
     * @return true if the arguments match, false otherwise
     */
    public boolean checkArguments(List<Object> arguments) {
        if (arguments.size() > parameters.size()) {
            return false;
        }
        
        // Check required parameters
        for (int i = 0; i < parameters.size(); i++) {
            FunctionParameter param = parameters.get(i);
            
            // If we've run out of arguments, check if the parameter is optional
            if (i >= arguments.size()) {
                if (param.getDefaultValue() == null) {
                    return false; // Missing required parameter
                }
                continue;
            }
            
            // Check argument type
            Object arg = arguments.get(i);
            if (arg == null && param.getDefaultValue() == null) {
                return false; // Null value for required parameter
            }
            
            // TODO: Add more sophisticated type checking
        }
        
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionSignature that = (FunctionSignature) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(parameters, that.parameters) &&
               Objects.equals(returnType, that.returnType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, parameters, returnType);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name);
        builder.append('(');
        
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameters.get(i));
        }
        
        builder.append(") -> ");
        builder.append(returnType.getTypeName());
        
        return builder.toString();
    }
}