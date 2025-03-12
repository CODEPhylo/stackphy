package io.github.stackphy.functions;

import io.github.stackphy.types.PhyloSpecType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a PhyloSpec distribution signature.
 */
public class DistributionSignature {
    private final String name;
    private final List<FunctionParameter> parameters;
    private final PhyloSpecType returnType;
    
    /**
     * Creates a new distribution signature.
     * 
     * @param name The distribution name
     * @param parameters The distribution parameters
     * @param returnType The distribution return type
     */
    public DistributionSignature(String name, List<FunctionParameter> parameters, PhyloSpecType returnType) {
        this.name = name;
        this.parameters = Collections.unmodifiableList(parameters);
        this.returnType = returnType;
    }
    
    /**
     * Gets the distribution name.
     * 
     * @return The distribution name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the distribution parameters.
     * 
     * @return The distribution parameters
     */
    public List<FunctionParameter> getParameters() {
        return parameters;
    }
    
    /**
     * Gets the distribution return type.
     * 
     * @return The distribution return type
     */
    public PhyloSpecType getReturnType() {
        return returnType;
    }
    
    /**
     * Checks if the given arguments match the distribution parameters.
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
                if (param.isRequired()) {
                    return false; // Missing required parameter
                }
                continue;
            }
            
            // Check argument type
            Object arg = arguments.get(i);
            if (arg == null && param.isRequired()) {
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
        DistributionSignature that = (DistributionSignature) o;
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