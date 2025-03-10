package io.github.stackphy.substitution;

import io.github.stackphy.model.Model;
import io.github.stackphy.model.Parameter;

/**
 * Implementation of the General Time Reversible (GTR) substitution model.
 */
public class GTR implements Model {
    private final Parameter rateParameters;
    private final Parameter baseFrequencies;
    
    /**
     * Creates a new GTR substitution model.
     * 
     * @param rateParameters The rate parameters
     *        Expected to be an array of 6 positive values
     *        (rates for AC, AG, AT, CG, CT, GT substitutions)
     * @param baseFrequencies The base frequencies parameter
     *        Expected to be an array of 4 values summing to 1.0
     *        (frequencies for A, C, G, T)
     */
    public GTR(Parameter rateParameters, Parameter baseFrequencies) {
        this.rateParameters = rateParameters;
        this.baseFrequencies = baseFrequencies;
        
        // Validate rate parameters if it's an array
        if (rateParameters.isArray()) {
            Object[] rates = rateParameters.getArrayValue();
            if (rates.length != 6) {
                throw new IllegalArgumentException("Rate parameters must be an array of length 6");
            }
            
            for (Object rate : rates) {
                if (!(rate instanceof Number)) {
                    throw new IllegalArgumentException("Rate parameters must be numeric");
                }
                double value = ((Number) rate).doubleValue();
                if (value <= 0.0) {
                    throw new IllegalArgumentException("Rate parameters must be positive");
                }
            }
        } else {
            throw new IllegalArgumentException("Rate parameters must be an array");
        }
        
        // Validate base frequencies if it's an array
        if (baseFrequencies.isArray()) {
            Object[] freqs = baseFrequencies.getArrayValue();
            if (freqs.length != 4) {
                throw new IllegalArgumentException("Base frequencies must be an array of length 4");
            }
            
            double sum = 0.0;
            for (Object freq : freqs) {
                if (!(freq instanceof Number)) {
                    throw new IllegalArgumentException("Base frequencies must be numeric");
                }
                double value = ((Number) freq).doubleValue();
                if (value < 0.0 || value > 1.0) {
                    throw new IllegalArgumentException("Base frequencies must be between 0 and 1");
                }
                sum += value;
            }
            
            // Allow for small floating-point errors
            if (Math.abs(sum - 1.0) > 1e-10) {
                throw new IllegalArgumentException("Base frequencies must sum to 1.0");
            }
        } else {
            throw new IllegalArgumentException("Base frequencies must be an array");
        }
    }
    
    @Override
    public String getModelType() {
        return "GTR";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { rateParameters, baseFrequencies };
    }
    
    /**
     * Gets the rate parameters.
     * 
     * @return The rate parameters
     */
    public Parameter getRateParameters() {
        return rateParameters;
    }
    
    /**
     * Gets the base frequencies parameter.
     * 
     * @return The base frequencies parameter
     */
    public Parameter getBaseFrequencies() {
        return baseFrequencies;
    }
    
    /**
     * Gets the current rate parameter values.
     * 
     * @return The rate parameter values as an array of doubles
     * @throws UnsupportedOperationException if the rates cannot be converted to doubles
     */
    public double[] getRateParameterValues() {
        Object[] rates = rateParameters.getArrayValue();
        double[] values = new double[rates.length];
        
        for (int i = 0; i < rates.length; i++) {
            if (rates[i] instanceof Number) {
                values[i] = ((Number) rates[i]).doubleValue();
            } else {
                throw new UnsupportedOperationException("Rate parameter is not numeric");
            }
        }
        
        return values;
    }
    
    /**
     * Gets the current base frequency values.
     * 
     * @return The base frequency values as an array of doubles
     * @throws UnsupportedOperationException if the frequencies cannot be converted to doubles
     */
    public double[] getBaseFrequencyValues() {
        Object[] freqs = baseFrequencies.getArrayValue();
        double[] values = new double[freqs.length];
        
        for (int i = 0; i < freqs.length; i++) {
            if (freqs[i] instanceof Number) {
                values[i] = ((Number) freqs[i]).doubleValue();
            } else {
                throw new UnsupportedOperationException("Base frequency is not numeric");
            }
        }
        
        return values;
    }
}
