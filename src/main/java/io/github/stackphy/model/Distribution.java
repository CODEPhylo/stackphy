package io.github.stackphy.model;

/**
 * Base interface for all probability distributions.
 */
public interface Distribution extends StackItem {
    /**
     * Returns the distribution type name.
     * 
     * @return The distribution type name (e.g., "lognormal", "dirichlet")
     */
    String getDistributionType();
    
    /**
     * Returns the parameters of this distribution.
     * 
     * @return Array of parameters
     */
    Parameter[] getParameters();
    
    /**
     * Generates a sample value from this distribution.
     * This is used for simulation or initialization.
     * 
     * @return A Primitive containing a value generated from this distribution
     */
    default Primitive generateValue() {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    default StackItemType getType() {
        return StackItemType.DISTRIBUTION;
    }
}
