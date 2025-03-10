package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Model;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.model.StackItemType;
import io.github.stackphy.model.Variable;

/**
 * Implementation of a Phylogenetic Continuous-Time Markov Chain.
 * This is the main distribution that connects a tree prior with a substitution model
 * to create a full phylogenetic model for sequence evolution.
 */
public class PhyloCTMC implements Distribution {
    private final Parameter tree;
    private final Parameter substitutionModel;
    private Parameter siteRates; // Optional gamma-distributed site rates
    private Parameter clockRate; // Optional clock rate
    
    /**
     * Creates a new PhyloCTMC model.
     * 
     * @param tree The tree parameter (must be a stochastic variable with a tree prior)
     * @param substitutionModel The substitution model parameter (HKY, GTR, etc.)
     */
    public PhyloCTMC(Parameter tree, Parameter substitutionModel) {
        this(tree, substitutionModel, null, null);
    }
    
    /**
     * Creates a new PhyloCTMC model with site rates and clock rate.
     * 
     * @param tree The tree parameter (must be a stochastic variable with a tree prior)
     * @param substitutionModel The substitution model parameter (HKY, GTR, etc.)
     * @param siteRates Optional gamma-distributed site rates parameter
     * @param clockRate Optional clock rate parameter
     */
    public PhyloCTMC(Parameter tree, Parameter substitutionModel, Parameter siteRates, Parameter clockRate) {
        this.tree = tree;
        this.substitutionModel = substitutionModel;
        this.siteRates = siteRates;
        this.clockRate = clockRate;
        
        // Validate tree parameter
        validateTreeParameter(tree);
        
        // Validate substitution model parameter
        validateSubstitutionModelParameter(substitutionModel);
        
        // Validate site rates if provided
        if (siteRates != null) {
            validateSiteRatesParameter(siteRates);
        }
        
        // Validate clock rate if provided
        if (clockRate != null) {
            validateClockRateParameter(clockRate);
        }
    }
    
    @Override
    public String getDistributionType() {
        return "phyloCTMC";
    }
    
    @Override
    public Parameter[] getParameters() {
        // Count non-null parameters
        int count = 2; // tree and substitutionModel are always required
        if (siteRates != null) count++;
        if (clockRate != null) count++;
        
        Parameter[] params = new Parameter[count];
        int index = 0;
        
        params[index++] = tree;
        params[index++] = substitutionModel;
        
        if (siteRates != null) {
            params[index++] = siteRates;
        }
        
        if (clockRate != null) {
            params[index++] = clockRate;
        }
        
        return params;
    }
    
    /**
     * Gets the tree parameter.
     * 
     * @return The tree parameter
     */
    public Parameter getTree() {
        return tree;
    }
    
    /**
     * Gets the substitution model parameter.
     * 
     * @return The substitution model parameter
     */
    public Parameter getSubstitutionModel() {
        return substitutionModel;
    }
    
    /**
     * Gets the site rates parameter.
     * 
     * @return The site rates parameter, or null if not set
     */
    public Parameter getSiteRates() {
        return siteRates;
    }
    
    /**
     * Sets the site rates parameter.
     * 
     * @param siteRates The site rates parameter
     * @throws IllegalArgumentException if the site rates parameter is invalid
     */
    public void setSiteRates(Parameter siteRates) {
        validateSiteRatesParameter(siteRates);
        this.siteRates = siteRates;
    }
    
    /**
     * Gets the clock rate parameter.
     * 
     * @return The clock rate parameter, or null if not set
     */
    public Parameter getClockRate() {
        return clockRate;
    }
    
    /**
     * Sets the clock rate parameter.
     * 
     * @param clockRate The clock rate parameter
     * @throws IllegalArgumentException if the clock rate parameter is invalid
     */
    public void setClockRate(Parameter clockRate) {
        validateClockRateParameter(clockRate);
        this.clockRate = clockRate;
    }
    
    /**
     * Validates that the tree parameter is valid.
     * 
     * @param tree The tree parameter to validate
     * @throws IllegalArgumentException if the tree parameter is invalid
     */
    private void validateTreeParameter(Parameter tree) {
        // The tree should ideally be a Variable with a tree distribution (Yule, BirthDeath, Coalescent)
        // For now, we'll just check that it's a Parameter
        if (tree == null) {
            throw new IllegalArgumentException("Tree parameter cannot be null");
        }
    }
    
    /**
     * Validates that the substitution model parameter is valid.
     * 
     * @param substitutionModel The substitution model parameter to validate
     * @throws IllegalArgumentException if the substitution model parameter is invalid
     */
    private void validateSubstitutionModelParameter(Parameter substitutionModel) {
        if (substitutionModel == null) {
            throw new IllegalArgumentException("Substitution model parameter cannot be null");
        }
        
        // If it's a Variable, try to get the underlying value
        if (substitutionModel instanceof Variable) {
            Variable var = (Variable) substitutionModel;
            StackItem value = var.getUnderlyingValue();
            
            if (value.getType() != StackItemType.MODEL) {
                throw new IllegalArgumentException("Substitution model variable must contain a model");
            }
        }
        // If it's a direct Model reference, that's fine too
    }
    
    /**
     * Validates that the site rates parameter is valid.
     * 
     * @param siteRates The site rates parameter to validate
     * @throws IllegalArgumentException if the site rates parameter is invalid
     */
    private void validateSiteRatesParameter(Parameter siteRates) {
        if (siteRates == null) {
            throw new IllegalArgumentException("Site rates parameter cannot be null");
        }
        
        // Ideally, this would check that it's a Gamma distribution or has specific properties
        // For now, we'll just check that it's a Parameter
    }
    
    /**
     * Validates that the clock rate parameter is valid.
     * 
     * @param clockRate The clock rate parameter to validate
     * @throws IllegalArgumentException if the clock rate parameter is invalid
     */
    private void validateClockRateParameter(Parameter clockRate) {
        if (clockRate == null) {
            throw new IllegalArgumentException("Clock rate parameter cannot be null");
        }
        
        // Ideally check that it's positive
        if (clockRate.isNumeric() && clockRate.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Clock rate must be positive");
        }
    }
}
