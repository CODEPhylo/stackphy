package io.github.stackphy.model;

/**
 * Interface for constraints in the model.
 * Constraints restrict the values of parameters.
 */
public interface Constraint extends StackItem {
    /**
     * Gets the type of constraint.
     * 
     * @return The constraint type
     */
    String getConstraintType();
    
    /**
     * Gets the parameters of the constraint.
     * 
     * @return The parameters
     */
    Parameter[] getParameters();
    
    /**
     * Gets the type of stack item.
     * 
     * @return CONSTRAINT
     */
    @Override
    default StackItemType getType() {
        return StackItemType.CONSTRAINT;
    }
}