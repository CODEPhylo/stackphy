package io.github.stackphy.constraints;

import io.github.stackphy.model.Constraint;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.StackItemType;

/**
 * Implementation of a "less than" constraint.
 * Ensures that one parameter is less than another parameter.
 */
public class LessThan implements Constraint {
    private final Parameter left;
    private final Parameter right;
    
    /**
     * Creates a new "less than" constraint.
     * 
     * @param left The parameter that should be less
     * @param right The parameter that should be greater
     */
    public LessThan(Parameter left, Parameter right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String getConstraintType() {
        return "lessThan";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { left, right };
    }
    
    /**
     * Gets the left parameter.
     * 
     * @return The left parameter
     */
    public Parameter getLeft() {
        return left;
    }
    
    /**
     * Gets the right parameter.
     * 
     * @return The right parameter
     */
    public Parameter getRight() {
        return right;
    }
    
    /**
     * Checks if the constraint is satisfied.
     * 
     * @return true if the constraint is satisfied, false otherwise
     */
    public boolean isSatisfied() {
        if (left.isNumeric() && right.isNumeric()) {
            return left.getDoubleValue() < right.getDoubleValue();
        }
        
        // If not numeric, we can't compare them
        throw new UnsupportedOperationException("Cannot compare non-numeric parameters");
    }
    
    @Override
    public StackItemType getType() {
        return StackItemType.CONSTRAINT;
    }
}