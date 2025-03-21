package io.github.stackphy.distribution;

import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItemType;

/**
 * Implementation of a discrete gamma distribution that produces a vector of rates.
 * Used for modeling rate heterogeneity across sites with a finite number of rate categories.
 * Can be used both as a regular DiscreteGamma distribution and as a DiscreteGammaVector.
 */
public class DiscreteGamma implements Distribution {
    private final Parameter shape;
    private final Parameter categories;
    private Parameter dimension;  // Optional dimension parameter (number of sites)
    private Parameter[] rateCategories; // Calculated rate categories
    
    /**
     * Creates a new discrete gamma distribution.
     * 
     * @param shape The shape parameter
     * @param categories The number of rate categories
     */
    public DiscreteGamma(Parameter shape, Parameter categories) {
        this(shape, categories, null);
    }
    
    /**
     * Creates a new discrete gamma vector distribution with specified dimension.
     * 
     * @param shape The shape parameter
     * @param categories The number of rate categories
     * @param dimension The dimension (number of sites)
     */
    public DiscreteGamma(Parameter shape, Parameter categories, Parameter dimension) {
        this.shape = shape;
        this.categories = categories;
        this.dimension = dimension;
        
        // Validate parameters
        if (shape.isNumeric() && shape.getDoubleValue() <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        
        if (categories.isNumeric()) {
            int numCategories = (int) categories.getDoubleValue();
            if (numCategories <= 0) {
                throw new IllegalArgumentException("Number of categories must be positive");
            }
        }
        
        if (dimension != null && dimension.isNumeric()) {
            int dim = (int) dimension.getDoubleValue();
            if (dim <= 0) {
                throw new IllegalArgumentException("Dimension must be positive");
            }
        }
        
        // Rate categories will be calculated when needed
        this.rateCategories = null;
    }
    
    @Override
    public String getDistributionType() {
        return dimension == null ? "discreteGamma" : "discreteGammaVector";
    }
    
    @Override
    public Parameter[] getParameters() {
        if (dimension == null) {
            return new Parameter[] { shape, categories };
        } else {
            return new Parameter[] { shape, categories, dimension };
        }
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
     * Gets the categories parameter.
     * 
     * @return The categories parameter
     */
    public Parameter getCategories() {
        return categories;
    }
    
    /**
     * Gets the dimension parameter.
     * 
     * @return The dimension parameter, or null if not specified
     */
    public Parameter getDimension() {
        return dimension;
    }
    
    /**
     * Gets the shape value.
     * 
     * @return The shape value
     */
    public double getShapeValue() {
        return shape.getDoubleValue();
    }
    
    /**
     * Gets the number of categories.
     * 
     * @return The number of categories
     */
    public int getCategoriesValue() {
        return (int) categories.getDoubleValue();
    }
    
    /**
     * Gets the dimension (number of sites).
     * 
     * @return The dimension value, or 0 if not specified
     */
    public int getDimensionValue() {
        return dimension != null ? (int) dimension.getDoubleValue() : 0;
    }
    
    /**
     * Gets the rate categories.
     * The rates are calculated to have a mean of 1.0 across all categories.
     * 
     * @return The rate categories
     */
    public Parameter[] getRateCategories() {
        if (rateCategories == null) {
            calculateRateCategories();
        }
        return rateCategories;
    }
    
    /**
     * Draws a sample from the distribution.
     * If dimension is specified, returns a vector of rates of that dimension.
     * Otherwise, returns a single rate category.
     * 
     * @return A parameter representing the sample
     */
    public Parameter drawSample() {
        if (rateCategories == null) {
            calculateRateCategories();
        }
        
        if (dimension == null) {
            // For DiscreteGamma, return a single rate
            int categoryIndex = (int) (Math.random() * rateCategories.length);
            return rateCategories[categoryIndex];
        } else {
            // For DiscreteGammaVector, return a vector of rates
            int dim = getDimensionValue();
            Parameter[] rateVector = new Parameter[dim];
            int numCategories = rateCategories.length;
            
            // Randomly assign categories to sites
            for (int i = 0; i < dim; i++) {
                int categoryIndex = (int) (Math.random() * numCategories);
                rateVector[i] = rateCategories[categoryIndex];
            }
            
            // Return the vector of rates as a Primitive array
            return new Primitive(rateVector);
        }
    }
    
    /**
     * Calculates the rate categories based on shape and number of categories.
     * This is a simplified implementation that approximates the discrete gamma distribution.
     */
    private void calculateRateCategories() {
        int numCategories = getCategoriesValue();
        double alpha = getShapeValue();
        rateCategories = new Parameter[numCategories];
        
        // For simplicity, use equal probabilities for each category
        // In a real implementation, we would use the cumulative distribution function
        // to find the mean value for each quantile
        
        // This is a very simplified calculation
        // A real implementation would use mean values for each category
        // based on proper numerical integration
        for (int i = 0; i < numCategories; i++) {
            // Simple approximation using midpoint of each category
            double lowerQuantile = (double) i / numCategories;
            double upperQuantile = (double) (i + 1) / numCategories;
            double midQuantile = (lowerQuantile + upperQuantile) / 2;
            
            // Approximate the rate
            // In practice, this should be calculated using proper gamma quantile functions
            // This is just a placeholder formula
            double rate = alpha * (1.0 + (midQuantile - 0.5) * 2.0 / Math.sqrt(alpha));
            
            rateCategories[i] = new Primitive(rate);
        }
        
        // Normalize to ensure mean rate is 1.0
        double sum = 0.0;
        for (Parameter p : rateCategories) {
            sum += p.getDoubleValue();
        }
        double meanRate = sum / numCategories;
        
        for (int i = 0; i < numCategories; i++) {
            double normalizedRate = rateCategories[i].getDoubleValue() / meanRate;
            rateCategories[i] = new Primitive(normalizedRate);
        }
    }
    
    @Override
    public StackItemType getType() {
        return StackItemType.DISTRIBUTION;
    }
}