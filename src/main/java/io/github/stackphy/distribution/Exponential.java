package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Primitive;

/**
 * Implementation of an exponential distribution.
 */
public class Exponential implements Distribution {
    private final Parameter rate;
    
    /**
     * Creates a new exponential distribution.
     * 
     * @param rate The rate parameter (λ)
     */
    public Exponential(Parameter rate) {
        this.rate = rate;
        
        // Validate rate is positive if it's a numeric parameter
        if (rate.isNumeric() && rate.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "exponential";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { rate };
    }
    
    @Override
    public Primitive generateValue() {
        double rate = getRateValue();
        // Simple exponential generation: -ln(U)/λ
        double value = -Math.log(Math.random()) / rate;
        return new Primitive(value);
    }

    
    /**
     * Gets the rate parameter.
     * 
     * @return The rate parameter
     */
    public Parameter getRate() {
        return rate;
    }
    
    /**
     * Gets the current rate value.
     * 
     * @return The rate value
     * @throws UnsupportedOperationException if the rate cannot be converted to a double
     */
    public double getRateValue() {
        return rate.getDoubleValue();
    }
}
