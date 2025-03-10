package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;

/**
 * Implementation of a Yule process for tree priors.
 * A pure-birth process (speciation without extinction).
 */
public class Yule implements Distribution {
    private final Parameter birthRate;
    
    /**
     * Creates a new Yule process.
     * 
     * @param birthRate The birth (speciation) rate parameter
     */
    public Yule(Parameter birthRate) {
        this.birthRate = birthRate;
        
        // Validate birth rate is positive if it's a numeric parameter
        if (birthRate.isNumeric() && birthRate.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Birth rate must be positive");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "yule";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { birthRate };
    }
    
    /**
     * Gets the birth rate parameter.
     * 
     * @return The birth rate parameter
     */
    public Parameter getBirthRate() {
        return birthRate;
    }
    
    /**
     * Gets the current birth rate value.
     * 
     * @return The birth rate value
     * @throws UnsupportedOperationException if the birth rate cannot be converted to a double
     */
    public double getBirthRateValue() {
        return birthRate.getDoubleValue();
    }
}
