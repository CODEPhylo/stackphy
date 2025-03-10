package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;

/**
 * Implementation of a gamma distribution.
 * Often used for modeling rate heterogeneity across sites.
 */
public class Gamma implements Distribution {
    private final Parameter shape;
    private final Parameter rate;
    
    /**
     * Creates a new gamma distribution.
     * 
     * @param shape The shape parameter (α)
     * @param rate The rate parameter (β)
     */
    public Gamma(Parameter shape, Parameter rate) {
        this.shape = shape;
        this.rate = rate;
        
        // Validate parameters are positive if they are numeric
        if (shape.isNumeric() && shape.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        
        if (rate.isNumeric() && rate.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Rate parameter must be positive");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "gamma";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { shape, rate };
    }
    
    /**
     * Gets the shape parameter.
     * 
     * @return The shape parameter
     */
    public Parameter getShape() {
        return shape;
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
     * Gets the current shape value.
     * 
     * @return The shape value
     * @throws UnsupportedOperationException if the shape cannot be converted to a double
     */
    public double getShapeValue() {
        return shape.getDoubleValue();
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
    
    /**
     * Gets the scale parameter (1/rate).
     * Some parameterizations of the gamma distribution use scale instead of rate.
     * 
     * @return The scale value
     * @throws UnsupportedOperationException if the rate cannot be converted to a double
     * @throws ArithmeticException if the rate is zero
     */
    public double getScaleValue() {
        double rateValue = getRateValue();
        if (rateValue == 0) {
            throw new ArithmeticException("Rate is zero, cannot calculate scale");
        }
        return 1.0 / rateValue;
    }
    
    /**
     * Gets the mean of the distribution (shape/rate).
     * 
     * @return The mean
     * @throws UnsupportedOperationException if the parameters cannot be converted to doubles
     * @throws ArithmeticException if the rate is zero
     */
    public double getMean() {
        return getShapeValue() / getRateValue();
    }
    
    /**
     * Gets the variance of the distribution (shape/rate²).
     * 
     * @return The variance
     * @throws UnsupportedOperationException if the parameters cannot be converted to doubles
     * @throws ArithmeticException if the rate is zero
     */
    public double getVariance() {
        double rateValue = getRateValue();
        return getShapeValue() / (rateValue * rateValue);
    }
}
