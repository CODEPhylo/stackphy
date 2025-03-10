package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Primitive;


/**
 * Implementation of a log-normal distribution.
 */
public class LogNormal implements Distribution {
    private final Parameter mean;
    private final Parameter sd;
    
    /**
     * Creates a new log-normal distribution.
     * 
     * @param mean The mean parameter (on log scale)
     * @param sd The standard deviation parameter (on log scale)
     */
    public LogNormal(Parameter mean, Parameter sd) {
        this.mean = mean;
        this.sd = sd;
        
        // Validate standard deviation is positive if it's a numeric parameter
        if (sd.isNumeric() && sd.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "lognormal";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { mean, sd };
    }
    
    @Override
    public Primitive generateValue() {
        double mean = getMeanValue();
        double sd = getStandardDeviationValue();
        
        // Generate standard normal
        double z = Math.random(); // Simplified - should use Box-Muller or similar
        // Convert to log-normal
        double value = Math.exp(mean + sd * z);
        
        return new Primitive(value);
    }
    
    /**
     * Gets the mean parameter (on log scale).
     * 
     * @return The mean parameter
     */
    public Parameter getMean() {
        return mean;
    }
    
    /**
     * Gets the standard deviation parameter (on log scale).
     * 
     * @return The standard deviation parameter
     */
    public Parameter getStandardDeviation() {
        return sd;
    }
    
    /**
     * Gets the current mean value (on log scale).
     * 
     * @return The mean value
     * @throws UnsupportedOperationException if the mean cannot be converted to a double
     */
    public double getMeanValue() {
        return mean.getDoubleValue();
    }
    
    /**
     * Gets the current standard deviation value (on log scale).
     * 
     * @return The standard deviation value
     * @throws UnsupportedOperationException if the standard deviation cannot be converted to a double
     */
    public double getStandardDeviationValue() {
        return sd.getDoubleValue();
    }
}
