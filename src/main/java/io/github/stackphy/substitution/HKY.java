package io.github.stackphy.substitution;

import io.github.stackphy.model.Model;
import io.github.stackphy.model.Parameter;

/**
 * Implementation of the Hasegawa-Kishino-Yano (HKY) substitution model.
 */
public class HKY implements Model {
    private final Parameter kappa;
    private final Parameter baseFrequencies;
    
    /**
     * Creates a new HKY substitution model.
     * 
     * @param kappa The transition/transversion ratio parameter
     * @param baseFrequencies The base frequencies parameter
     *        Expected to be an array of 4 values summing to 1.0
     *        (frequencies for A, C, G, T)
     */
    public HKY(Parameter kappa, Parameter baseFrequencies) {
        this.kappa = kappa;
        this.baseFrequencies = baseFrequencies;
        
        // Basic validation - avoid extensive validation on the actual values
        // since they might be references to stochastic variables
        // that haven't been evaluated yet
        
        // Maybe just validate the baseFrequencies will eventually provide an array
        try {
            // Just check if it can provide an array, don't use the result
            baseFrequencies.getArrayValue();
        } catch (UnsupportedOperationException e) {
            throw new IllegalArgumentException("Base frequencies must be capable of providing an array", e);
        }
        
        // Similarly for kappa, just check it can provide a numeric value
        try {
        // Just check if it can provide a numeric value, don't use the result
            kappa.getDoubleValue();
        } catch (UnsupportedOperationException e) {
            throw new IllegalArgumentException("Kappa must be capable of providing a numeric value", e);
        }
    }    
    @Override
    public String getModelType() {
        return "HKY";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { kappa, baseFrequencies };
    }
    
    /**
     * Gets the kappa parameter (transition/transversion ratio).
     * 
     * @return The kappa parameter
     */
    public Parameter getKappa() {
        return kappa;
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
     * Gets the current kappa value.
     * 
     * @return The kappa value
     * @throws UnsupportedOperationException if kappa cannot be converted to a double
     */
    public double getKappaValue() {
        return kappa.getDoubleValue();
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
