package io.github.stackphy.functions;

import io.github.stackphy.types.*;
import java.util.*;

/**
 * Registry of PhyloSpec distribution signatures.
 */
public class DistributionRegistry {
    private static final Map<String, DistributionSignature> DISTRIBUTIONS = new HashMap<>();
    
    static {
        registerStatisticalDistributions();
        registerTreeDistributions();
        registerSequenceEvolutionDistributions();
        registerMixtureDistributions();
    }
    
    /**
     * Gets a distribution signature by name.
     * 
     * @param name The distribution name
     * @return The distribution signature, or null if not found
     */
    public static DistributionSignature getDistribution(String name) {
        return DISTRIBUTIONS.get(name);
    }
    
    /**
     * Gets all distribution signatures.
     * 
     * @return Map of distribution names to signatures
     */
    public static Map<String, DistributionSignature> getAllDistributions() {
        return Collections.unmodifiableMap(DISTRIBUTIONS);
    }
    
    /**
     * Registers a distribution signature.
     * 
     * @param signature The distribution signature
     */
    private static void register(DistributionSignature signature) {
        DISTRIBUTIONS.put(signature.getName(), signature);
    }
    
    /**
     * Registers statistical distributions.
     */
    private static void registerStatisticalDistributions() {
        // Normal
        register(new DistributionSignature("Normal", 
                Arrays.asList(
                    new FunctionParameter("mean", PrimitiveType.REAL, "0.0"),
                    new FunctionParameter("sd", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PrimitiveType.REAL));
        
        // LogNormal
        register(new DistributionSignature("LogNormal", 
                Arrays.asList(
                    new FunctionParameter("meanlog", PrimitiveType.REAL, "0.0"),
                    new FunctionParameter("sdlog", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PrimitiveType.POSITIVE_REAL));
        
        // Gamma
        register(new DistributionSignature("Gamma", 
                Arrays.asList(
                    new FunctionParameter("shape", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("rate", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PrimitiveType.POSITIVE_REAL));
        
        // Beta
        register(new DistributionSignature("Beta", 
                Arrays.asList(
                    new FunctionParameter("alpha", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("beta", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PrimitiveType.PROBABILITY));
        
        // Exponential
        register(new DistributionSignature("Exponential", 
                Collections.singletonList(
                    new FunctionParameter("rate", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PrimitiveType.POSITIVE_REAL));
        
        // Uniform
        register(new DistributionSignature("Uniform", 
                Arrays.asList(
                    new FunctionParameter("lower", PrimitiveType.REAL, null),
                    new FunctionParameter("upper", PrimitiveType.REAL, null)
                ), 
                PrimitiveType.REAL));
        
        // Dirichlet
        register(new DistributionSignature("Dirichlet", 
                Collections.singletonList(
                    new FunctionParameter("alpha", CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL), null)
                ), 
                CollectionType.SIMPLEX));
        
        // MultivariateNormal
        register(new DistributionSignature("MultivariateNormal", 
                Arrays.asList(
                    new FunctionParameter("mean", CollectionType.REAL_VECTOR, null),
                    new FunctionParameter("covariance", CollectionType.REAL_MATRIX, null)
                ), 
                CollectionType.REAL_VECTOR));
    }
    
    /**
     * Registers tree distributions.
     */
    private static void registerTreeDistributions() {
        // Yule
        register(new DistributionSignature("Yule", 
                Collections.singletonList(
                    new FunctionParameter("birthRate", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PhylogeneticType.TREE));
        
        // BirthDeath
        register(new DistributionSignature("BirthDeath", 
                Arrays.asList(
                    new FunctionParameter("birthRate", PrimitiveType.POSITIVE_REAL, null),
                    new FunctionParameter("deathRate", PrimitiveType.POSITIVE_REAL, null),
                    new FunctionParameter("rootHeight", PrimitiveType.POSITIVE_REAL, null)
                ), 
                PhylogeneticType.TREE));
        
        // Coalescent
        register(new DistributionSignature("Coalescent", 
                Collections.singletonList(
                    new FunctionParameter("populationSize", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                PhylogeneticType.TREE));
        
        // FossilBirthDeath
        register(new DistributionSignature("FossilBirthDeath", 
                Arrays.asList(
                    new FunctionParameter("birthRate", PrimitiveType.POSITIVE_REAL, null),
                    new FunctionParameter("deathRate", PrimitiveType.POSITIVE_REAL, null),
                    new FunctionParameter("samplingRate", PrimitiveType.POSITIVE_REAL, null),
                    new FunctionParameter("rho", PrimitiveType.PROBABILITY, null)
                ), 
                PhylogeneticType.TIME_TREE));
    }
    
    /**
     * Registers sequence evolution distributions.
     */
    private static void registerSequenceEvolutionDistributions() {
        // PhyloCTMC
        // Use a generic type string directly for now
        GenericType alignmentType = new GenericType("Alignment<A>");
        
        register(new DistributionSignature("PhyloCTMC", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("Q", CollectionType.Q_MATRIX, null),
                    new FunctionParameter("siteRates", CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL), null),
                    new FunctionParameter("branchRates", CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL), null)
                ), 
                // Use PhylogeneticType.DNA_ALIGNMENT instead since GenericType can't be cast
                PhylogeneticType.DNA_ALIGNMENT));
        
        // PhyloBM
        register(new DistributionSignature("PhyloBM", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("sigma", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("rootValue", PrimitiveType.REAL, "0.0")
                ), 
                CollectionType.REAL_VECTOR));
        
        // PhyloOU
        register(new DistributionSignature("PhyloOU", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("sigma", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("alpha", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("optimum", PrimitiveType.REAL, "0.0")
                ), 
                CollectionType.REAL_VECTOR));
    }
    
    /**
     * Registers mixture distributions.
     */
    private static void registerMixtureDistributions() {
        // Mixture
        GenericType typeT = new GenericType("T");
        GenericType distT = new GenericType("Distribution<T>");
        
        // For now, use a more concrete return type since we can't cast GenericType to what's expected
        register(new DistributionSignature("Mixture", 
                Arrays.asList(
                    new FunctionParameter("components", CollectionType.vectorOf(distT), null),
                    new FunctionParameter("weights", CollectionType.SIMPLEX, null)
                ), 
                // Return a real value as a generic placeholder
                PrimitiveType.REAL));
        
        // DiscreteGammaMixture
        register(new DistributionSignature("DiscreteGammaMixture", 
                Arrays.asList(
                    new FunctionParameter("shape", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("categories", PrimitiveType.POS_INTEGER, "4")
                ), 
                // Return positive real values
                PrimitiveType.POSITIVE_REAL));
    }
}