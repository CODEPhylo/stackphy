package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;

/**
 * Implementation of a birth-death process for tree priors.
 * Models speciation (birth) and extinction (death) rates.
 */
public class BirthDeath implements Distribution {
    private final Parameter birthRate;
    private final Parameter deathRate;
    
    /**
     * Creates a new birth-death process.
     * 
     * @param birthRate The birth (speciation) rate parameter
     * @param deathRate The death (extinction) rate parameter
     */
    public BirthDeath(Parameter birthRate, Parameter deathRate) {
        this.birthRate = birthRate;
        this.deathRate = deathRate;
        
        // Validate rates are positive if they are numeric parameters
        if (birthRate.isNumeric() && birthRate.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Birth rate must be positive");
        }
        
        if (deathRate.isNumeric() && deathRate.getDoubleValue() < 0) {
            throw new IllegalArgumentException("Death rate must be non-negative");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "birthDeath";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { birthRate, deathRate };
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
     * Gets the death rate parameter.
     * 
     * @return The death rate parameter
     */
    public Parameter getDeathRate() {
        return deathRate;
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
    
    /**
     * Gets the current death rate value.
     * 
     * @return The death rate value
     * @throws UnsupportedOperationException if the death rate cannot be converted to a double
     */
    public double getDeathRateValue() {
        return deathRate.getDoubleValue();
    }
    
    /**
     * Gets the net diversification rate (birth rate - death rate).
     * 
     * @return The net diversification rate
     * @throws UnsupportedOperationException if either rate cannot be converted to a double
     */
    public double getNetDiversificationRate() {
        return getBirthRateValue() - getDeathRateValue();
    }
    
    /**
     * Gets the relative extinction rate (death rate / birth rate).
     * 
     * @return The relative extinction rate
     * @throws UnsupportedOperationException if either rate cannot be converted to a double
     */
    public double getRelativeExtinctionRate() {
        double birth = getBirthRateValue();
        if (birth == 0) {
            throw new ArithmeticException("Birth rate is zero, cannot calculate relative extinction");
        }
        return getDeathRateValue() / birth;
    }
}
