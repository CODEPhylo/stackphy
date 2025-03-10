package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Primitive;

/**
 * Implementation of a Dirichlet distribution.
 */
public class Dirichlet implements Distribution {
    private final Parameter concentrationParams;
    
    /**
     * Creates a new Dirichlet distribution.
     * 
     * @param concentrationParams The concentration parameters (alpha)
     *        Expected to be an array of positive numbers
     */
    public Dirichlet(Parameter concentrationParams) {
        this.concentrationParams = concentrationParams;
        
        // Validate concentration parameters if it's an array
        if (concentrationParams.isArray()) {
        Object[] alphas = concentrationParams.getArrayValue();
        for (Object alpha : alphas) {
            if (!(alpha instanceof Number)) {
            throw new IllegalArgumentException("Concentration parameters must be numeric");
            }
            if (((Number) alpha).doubleValue() <= 0) {
            throw new IllegalArgumentException("Concentration parameters must be positive");
            }
        }
        } else {
        throw new IllegalArgumentException("Concentration parameters must be an array");
        }
    }
    
    @Override
    public String getDistributionType() {
        return "dirichlet";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { concentrationParams };
    }
    
    @Override
    public Primitive generateValue() {
        // Get concentration parameters
        Object[] alphas = concentrationParams.getArrayValue();
        double[] concentrations = new double[alphas.length];
        
        for (int i = 0; i < alphas.length; i++) {
            concentrations[i] = ((Number) alphas[i]).doubleValue();
        }
        
        // Generate Dirichlet sample (simplified algorithm)
        double[] sample = new double[concentrations.length];
        double sum = 0.0;
        
        // Generate gamma samples and normalize
        for (int i = 0; i < concentrations.length; i++) {
            // In actual implementation, use proper gamma sampling
            double gammaValue = Math.random() * concentrations[i]; 
            sample[i] = gammaValue;
            sum += gammaValue;
        }
        
        // Normalize to get Dirichlet sample
        for (int i = 0; i < sample.length; i++) {
            sample[i] /= sum;
        }
        
        // Convert to Double[] for storage in Primitive
        Double[] result = new Double[sample.length];
        for (int i = 0; i < sample.length; i++) {
            result[i] = sample[i];
        }
        
        return new Primitive(result);
    }
    
    /**
     * Gets the concentration parameters.
     * 
     * @return The concentration parameters
     */
    public Parameter getConcentrationParameters() {
        return concentrationParams;
    }
    
    /**
     * Gets the current concentration parameter values.
     * 
     * @return The concentration parameter values as an array of doubles
     * @throws UnsupportedOperationException if the parameters cannot be converted to doubles
     */
    public double[] getConcentrationParameterValues() {
        Object[] alphas = concentrationParams.getArrayValue();
        double[] values = new double[alphas.length];
        
        for (int i = 0; i < alphas.length; i++) {
            if (alphas[i] instanceof Number) {
                values[i] = ((Number) alphas[i]).doubleValue();
            } else {
                throw new UnsupportedOperationException("Concentration parameter is not numeric");
            }
        }
        
        return values;
    }
    
    /**
     * Gets the dimension of the Dirichlet distribution.
     * 
     * @return The dimension (number of concentration parameters)
     */
    public int getDimension() {
        return concentrationParams.getArrayValue().length;
    }
}
