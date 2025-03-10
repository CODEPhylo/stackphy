package io.github.stackphy.model;

/**
 * Base interface for substitution models.
 */
public interface Model extends StackItem {
    /**
     * Returns the model type name.
     * 
     * @return The model type name (e.g., "HKY", "GTR")
     */
    String getModelType();
    
    /**
     * Returns the parameters of this model.
     * 
     * @return Array of parameters
     */
    Parameter[] getParameters();
    
    @Override
    default StackItemType getType() {
        return StackItemType.MODEL;
    }
}
