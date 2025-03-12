package io.github.stackphy.functions;

import io.github.stackphy.types.*;
import java.util.*;

/**
 * Registry of PhyloSpec constraint signatures.
 */
public class ConstraintRegistry {
    private static final Map<String, FunctionSignature> CONSTRAINTS = new HashMap<>();
    
    static {
        registerNumericConstraints();
        registerTreeConstraints();
        registerSequenceConstraints();
        registerComplexConstraints();
    }
    
    /**
     * Gets a constraint signature by name.
     * 
     * @param name The constraint name
     * @return The constraint signature, or null if not found
     */
    public static FunctionSignature getConstraint(String name) {
        return CONSTRAINTS.get(name);
    }
    
    /**
     * Gets all constraint signatures.
     * 
     * @return Map of constraint names to signatures
     */
    public static Map<String, FunctionSignature> getAllConstraints() {
        return Collections.unmodifiableMap(CONSTRAINTS);
    }
    
    /**
     * Registers a constraint signature.
     * 
     * @param signature The constraint signature
     */
    private static void register(FunctionSignature signature) {
        CONSTRAINTS.put(signature.getName(), signature);
    }
    
    /**
     * Registers numeric constraints.
     */
    private static void registerNumericConstraints() {
        // LessThan
        register(new FunctionSignature("LessThan", 
                Arrays.asList(
                    new FunctionParameter("left", PrimitiveType.REAL, null),
                    new FunctionParameter("right", PrimitiveType.REAL, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // GreaterThan
        register(new FunctionSignature("GreaterThan", 
                Arrays.asList(
                    new FunctionParameter("left", PrimitiveType.REAL, null),
                    new FunctionParameter("right", PrimitiveType.REAL, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // Equals
        GenericType typeT = new GenericType("T");
        register(new FunctionSignature("Equals", 
                Arrays.asList(
                    new FunctionParameter("left", typeT, null),
                    new FunctionParameter("right", typeT, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // Bounded
        register(new FunctionSignature("Bounded", 
                Arrays.asList(
                    new FunctionParameter("variable", PrimitiveType.REAL, null),
                    new FunctionParameter("lower", PrimitiveType.REAL, null),
                    new FunctionParameter("upper", PrimitiveType.REAL, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // SumTo
        register(new FunctionSignature("SumTo", 
                Arrays.asList(
                    new FunctionParameter("variables", CollectionType.REAL_VECTOR, null),
                    new FunctionParameter("target", PrimitiveType.REAL, null)
                ), 
                PhylogeneticType.CONSTRAINT));
    }
    
    /**
     * Registers tree constraints.
     */
    private static void registerTreeConstraints() {
        // Monophyly
        register(new FunctionSignature("Monophyly", 
                Arrays.asList(
                    new FunctionParameter("taxa", PhylogeneticType.TAXON_SET, null),
                    new FunctionParameter("tree", PhylogeneticType.TREE, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // Calibration
        GenericType distReal = new GenericType("Distribution<Real>");
        register(new FunctionSignature("Calibration", 
                Arrays.asList(
                    new FunctionParameter("node", PhylogeneticType.TREE_NODE, null),
                    new FunctionParameter("distribution", distReal, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // FixedTopology
        register(new FunctionSignature("FixedTopology", 
                Arrays.asList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null),
                    new FunctionParameter("topology", PhylogeneticType.TREE, null)
                ), 
                PhylogeneticType.CONSTRAINT));
    }
    
    /**
     * Registers sequence constraints.
     */
    private static void registerSequenceConstraints() {
        // MolecularClock
        register(new FunctionSignature("MolecularClock", 
                Collections.singletonList(
                    new FunctionParameter("tree", PhylogeneticType.TREE, null)
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // SitePattern
        GenericType alphabetType = new GenericType("A");
        GenericType alignmentType = new GenericType("Alignment<A>");
        GenericType vectorA = new GenericType("Vector<A>");
        
        register(new FunctionSignature("SitePattern", 
                Arrays.asList(
                    new FunctionParameter("alignment", alignmentType, null),
                    new FunctionParameter("pattern", vectorA, null),
                    new FunctionParameter("indices", CollectionType.INT_VECTOR, null)
                ), 
                PhylogeneticType.CONSTRAINT));
    }
    
    /**
     * Registers complex constraints.
     */
    private static void registerComplexConstraints() {
        // CompoundConstraint
        register(new FunctionSignature("CompoundConstraint", 
                Arrays.asList(
                    new FunctionParameter("constraints", CollectionType.vectorOf(PhylogeneticType.CONSTRAINT), null),
                    new FunctionParameter("operator", PrimitiveType.STRING, "\"AND\"")
                ), 
                PhylogeneticType.CONSTRAINT));
        
        // Correlation
        register(new FunctionSignature("Correlation", 
                Arrays.asList(
                    new FunctionParameter("variables", CollectionType.REAL_VECTOR, null),
                    new FunctionParameter("threshold", PrimitiveType.PROBABILITY, "0.0")
                ), 
                PhylogeneticType.CONSTRAINT));
    }
}