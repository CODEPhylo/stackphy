package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Primitive;

/**
 * Implementation of a normal (Gaussian) distribution.
 */
public class Normal implements Distribution {
    private final Parameter mean;
    private final Parameter sd;
    
    /**
     * Creates a new normal distribution.
     * 
     * @param mean The mean parameter
     * @param sd The standard deviation parameter
     */
    public Normal(Parameter mean, Parameter sd) {
        this.mean = mean;
        this.sd = sd;
        
        // Validate standard deviation is positive if it's a numeric parameter
        if (sd.isNumeric() && sd.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "normal";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { mean, sd };
    }
    
    @Override
    public Primitive generateValue() {
        // Get mean and standard deviation
        double mean = getMeanValue();
        double sd = getStandardDeviationValue();
        
        // Generate a normal random value (simplified)
        // In a real implementation, you'd use a proper random number generator
        double u1 = Math.random();
        double u2 = Math.random();
        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
        double value = mean + sd * z;
        
        return new Primitive(value);
    }
    
    /**
     * Gets the mean parameter.
     * 
     * @return The mean parameter
     */
    public Parameter getMean() {
        return mean;
    }
    
    /**
     * Gets the standard deviation parameter.
     * 
     * @return The standard deviation parameter
     */
    public Parameter getStandardDeviation() {
        return sd;
    }
    
    /**
     * Gets the current mean value.
     * 
     * @return The mean value
     * @throws UnsupportedOperationException if the mean cannot be converted to a double
     */
    public double getMeanValue() {
        return mean.getDoubleValue();
    }
    
    /**
     * Gets the current standard deviation value.
     * 
     * @return The standard deviation value
     * @throws UnsupportedOperationException if the standard deviation cannot be converted to a double
     */
    public double getStandardDeviationValue() {
        return sd.getDoubleValue();
    }
}
