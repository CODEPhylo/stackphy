package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;

/**
 * Implementation of a coalescent process for tree priors.
 * Models the genealogy of a sample from a population.
 */
public class Coalescent implements Distribution {
    private final Parameter populationSize;
    
    /**
     * Creates a new coalescent process.
     * 
     * @param populationSize The effective population size parameter
     */
    public Coalescent(Parameter populationSize) {
        this.populationSize = populationSize;
        
        // Validate population size is positive if it's a numeric parameter
        if (populationSize.isNumeric() && populationSize.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Population size must be positive");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "coalescent";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { populationSize };
    }
    
    /**
     * Gets the population size parameter.
     * 
     * @return The population size parameter
     */
    public Parameter getPopulationSize() {
        return populationSize;
    }
    
    /**
     * Gets the current population size value.
     * 
     * @return The population size value
     * @throws UnsupportedOperationException if the population size cannot be converted to a double
     */
    public double getPopulationSizeValue() {
        return populationSize.getDoubleValue();
    }
}
