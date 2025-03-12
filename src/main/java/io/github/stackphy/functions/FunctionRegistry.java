package io.github.stackphy.functions;

import io.github.stackphy.types.*;
import java.util.*;

/**
 * Registry of PhyloSpec function signatures.
 */
public class FunctionRegistry {
    private static final Map<String, FunctionSignature> FUNCTIONS = new HashMap<>();
    
    static {
        registerSubstitutionModels();
        registerRateHeterogeneityFunctions();
        registerTreeFunctions();
        registerMathFunctions();
    }
    
    /**
     * Gets a function signature by name.
     * 
     * @param name The function name
     * @return The function signature, or null if not found
     */
    public static FunctionSignature getFunction(String name) {
        return FUNCTIONS.get(name);
    }
    
    /**
     * Gets all function signatures.
     * 
     * @return Map of function names to signatures
     */
    public static Map<String, FunctionSignature> getAllFunctions() {
        return Collections.unmodifiableMap(FUNCTIONS);
    }
    
    /**
     * Registers a function signature.
     * 
     * @param signature The function signature
     */
    private static void register(FunctionSignature signature) {
        FUNCTIONS.put(signature.getName(), signature);
    }
    
    /**
     * Registers substitution model functions.
     */
    private static void registerSubstitutionModels() {
        // JC69
        register(new FunctionSignature("JC69", Collections.emptyList(), CollectionType.Q_MATRIX));
        
        // K80
        register(new FunctionSignature("K80", 
                Collections.singletonList(
                    new FunctionParameter("kappa", PrimitiveType.POSITIVE_REAL, "2.0")
                ), 
                CollectionType.Q_MATRIX));
        
        // F81
        register(new FunctionSignature("F81", 
                Collections.singletonList(
                    new FunctionParameter("baseFrequencies", CollectionType.SIMPLEX, null)
                ), 
                CollectionType.Q_MATRIX));
        
        // HKY
        register(new FunctionSignature("HKY", 
                Arrays.asList(
                    new FunctionParameter("kappa", PrimitiveType.POSITIVE_REAL, "2.0"),
                    new FunctionParameter("baseFrequencies", CollectionType.SIMPLEX, null)
                ), 
                CollectionType.Q_MATRIX));
        
        // GTR
        register(new FunctionSignature("GTR", 
                Arrays.asList(
                    new FunctionParameter("rateMatrix", CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL), null),
                    new FunctionParameter("baseFrequencies", CollectionType.SIMPLEX, null)
                ), 
                CollectionType.Q_MATRIX));
        
        // WAG
        register(new FunctionSignature("WAG", 
                Collections.singletonList(
                    new FunctionParameter("freqsModel", PrimitiveType.BOOLEAN, "true")
                ), 
                CollectionType.Q_MATRIX));
        
        // JTT
        register(new FunctionSignature("JTT", 
                Collections.singletonList(
                    new FunctionParameter("freqsModel", PrimitiveType.BOOLEAN, "true")
                ), 
                CollectionType.Q_MATRIX));
        
        // LG
        register(new FunctionSignature("LG", 
                Collections.singletonList(
                    new FunctionParameter("freqsModel", PrimitiveType.BOOLEAN, "true")
                ), 
                CollectionType.Q_MATRIX));
        
        // GY94
        register(new FunctionSignature("GY94", 
                Arrays.asList(
                    new FunctionParameter("omega", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("kappa", PrimitiveType.POSITIVE_REAL, "2.0"),
                    new FunctionParameter("codonFrequencies", CollectionType.SIMPLEX, null)
                ), 
                CollectionType.Q_MATRIX));
    }
    
    /**
     * Registers rate heterogeneity functions.
     */
    private static void registerRateHeterogeneityFunctions() {
        // DiscreteGamma
        register(new FunctionSignature("DiscreteGamma", 
                Arrays.asList(
                    new FunctionParameter("shape", PrimitiveType.POSITIVE_REAL, "1.0"),
                    new FunctionParameter("categories", PrimitiveType.POS_INTEGER, "4")
                ), 
                CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL)));
        
        // FreeRates
        register(new FunctionSignature("FreeRates", 
                Arrays.asList(
                    new FunctionParameter("rates", CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL), null),
                    new FunctionParameter("weights", CollectionType.SIMPLEX, null)
                ), 
                CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL)));
        
        // InvariantSites
        register(new FunctionSignature("InvariantSites", 
                Collections.singletonList(
                    new FunctionParameter("proportion", PrimitiveType.PROBABILITY, "0.0")
                ), 
                CollectionType.vectorOf(PrimitiveType.REAL)));
        
        // StrictClock
        register(new FunctionSignature("StrictClock", 
                Collections.singletonList(
                    new FunctionParameter("rate", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL)));
        
        // UncorrelatedLognormal
        register(new FunctionSignature("UncorrelatedLognormal", 
                Arrays.asList(
                    new FunctionParameter("mean", PrimitiveType.REAL, "0.0"),
                    new FunctionParameter("stdev", PrimitiveType.POSITIVE_REAL, "0.5")
                ), 
                CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL)));
        
        // UncorrelatedExponential
        register(new FunctionSignature("UncorrelatedExponential", 
                Collections.singletonList(
                    new FunctionParameter("mean", PrimitiveType.POSITIVE_REAL, "1.0")
                ), 
                CollectionType.vectorOf(PrimitiveType.POSITIVE_REAL)));
    }
    
    /**
     * Registers tree functions.
     */
    private static void registerTreeFunctions() {
        // mrca
        register(new FunctionSignature("mrca", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("taxa", PhylogeneticType.TAXON_SET, null)
                ), 
                PhylogeneticType.TREE_NODE));
        
        // treeHeight
        register(new FunctionSignature("treeHeight", 
                Collections.singletonList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null)
                ), 
                PrimitiveType.REAL));
        
        // nodeAge
        register(new FunctionSignature("nodeAge", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TIME_TREE, null),
                    new FunctionParameter("node", PhylogeneticType.TREE_NODE, null)
                ), 
                PrimitiveType.REAL));
        
        // branchLength
        register(new FunctionSignature("branchLength", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("node", PhylogeneticType.TREE_NODE, null)
                ), 
                PrimitiveType.REAL));
        
        // distanceMatrix
        register(new FunctionSignature("distanceMatrix", 
                Collections.singletonList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null)
                ), 
                CollectionType.REAL_MATRIX));
        
        // descendantTaxa
        register(new FunctionSignature("descendantTaxa", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("node", PhylogeneticType.TREE_NODE, null)
                ), 
                PhylogeneticType.TAXON_SET));
    }
    
    /**
     * Registers mathematical functions.
     */
    private static void registerMathFunctions() {
        // Create a generic type T for vectorElement and matrixElement
        GenericType typeT = new GenericType("T");
        
        // vectorElement
        register(new FunctionSignature("vectorElement", 
                Arrays.asList(
                    new FunctionParameter("vector", CollectionType.vectorOf(typeT), null),
                    new FunctionParameter("index", PrimitiveType.INTEGER, null)
                ), 
                typeT));
        
        // matrixElement
        register(new FunctionSignature("matrixElement", 
                Arrays.asList(
                    new FunctionParameter("matrix", CollectionType.matrixOf(typeT), null),
                    new FunctionParameter("row", PrimitiveType.INTEGER, null),
                    new FunctionParameter("col", PrimitiveType.INTEGER, null)
                ), 
                typeT));
        
        // scale
        register(new FunctionSignature("scale", 
                Arrays.asList(
                    new FunctionParameter("vector", CollectionType.REAL_VECTOR, null),
                    new FunctionParameter("factor", PrimitiveType.REAL, null)
                ), 
                CollectionType.REAL_VECTOR));
        
        // normalize
        register(new FunctionSignature("normalize", 
                Collections.singletonList(
                    new FunctionParameter("vector", CollectionType.REAL_VECTOR, null)
                ), 
                CollectionType.SIMPLEX));
        
        // log
        register(new FunctionSignature("log", 
                Collections.singletonList(
                    new FunctionParameter("x", PrimitiveType.POSITIVE_REAL, null)
                ), 
                PrimitiveType.REAL));
        
        // exp
        register(new FunctionSignature("exp", 
                Collections.singletonList(
                    new FunctionParameter("x", PrimitiveType.REAL, null)
                ), 
                PrimitiveType.POSITIVE_REAL));
        
        // sum
        register(new FunctionSignature("sum", 
                Collections.singletonList(
                    new FunctionParameter("vector", CollectionType.REAL_VECTOR, null)
                ), 
                PrimitiveType.REAL));
        
        // product
        register(new FunctionSignature("product", 
                Collections.singletonList(
                    new FunctionParameter("vector", CollectionType.REAL_VECTOR, null)
                ), 
                PrimitiveType.REAL));
    }
}