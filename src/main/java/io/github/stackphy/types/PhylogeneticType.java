package io.github.stackphy.types;

/**
 * Specialized phylogenetic types in the PhyloSpec type system.
 */
public class PhylogeneticType extends PhyloSpecType {
    // Tree types
    public static final PhylogeneticType TAXON = new PhylogeneticType("Taxon");
    public static final PhylogeneticType TAXON_SET = new PhylogeneticType("TaxonSet");
    public static final PhylogeneticType TREE_NODE = new PhylogeneticType("TreeNode");
    public static final PhylogeneticType TREE = new PhylogeneticType("Tree");
    public static final PhylogeneticType TIME_TREE = new SubType("TimeTree", TREE);
    
    // Sequence types
    public static final PhylogeneticType NUCLEOTIDE = new PhylogeneticType("Nucleotide");
    public static final PhylogeneticType AMINO_ACID = new PhylogeneticType("AminoAcid");
    public static final PhylogeneticType CODON = new PhylogeneticType("Codon");
    
    public static final PhylogeneticType DNA_SEQUENCE = new PhylogeneticType("DNASequence");
    public static final PhylogeneticType PROTEIN_SEQUENCE = new PhylogeneticType("ProteinSequence");
    public static final PhylogeneticType CODON_SEQUENCE = new PhylogeneticType("CodonSequence");
    
    public static final PhylogeneticType DNA_ALIGNMENT = new PhylogeneticType("DNAAlignment");
    public static final PhylogeneticType PROTEIN_ALIGNMENT = new PhylogeneticType("ProteinAlignment");
    public static final PhylogeneticType CODON_ALIGNMENT = new PhylogeneticType("CodonAlignment");
    
    // Constraint type
    public static final PhylogeneticType CONSTRAINT = new PhylogeneticType("Constraint");
    
    /**
     * Creates a new phylogenetic type with the given name.
     * 
     * @param typeName The name of the type
     */
    protected PhylogeneticType(String typeName) {
        super(typeName);
    }
    
    @Override
    public boolean isAssignableFrom(PhyloSpecType otherType) {
        if (otherType == this) {
            return true;
        }
        
        if (otherType instanceof SubType) {
            SubType subType = (SubType) otherType;
            return this.isAssignableFrom(subType.getBaseType());
        }
        
        return false;
    }
    
    /**
     * Creates a sequence type with the given alphabet.
     * 
     * @param alphabetType The alphabet type
     * @return The sequence type
     */
    public static PhylogeneticType sequenceOf(PhylogeneticType alphabetType) {
        return new ParameterizedType("Sequence", alphabetType);
    }
    
    /**
     * Creates an alignment type with the given alphabet.
     * 
     * @param alphabetType The alphabet type
     * @return The alignment type
     */
    public static PhylogeneticType alignmentOf(PhylogeneticType alphabetType) {
        return new ParameterizedType("Alignment", alphabetType);
    }
    
    /**
     * Subtype with a base type.
     */
    private static class SubType extends PhylogeneticType {
        private final PhylogeneticType baseType;
        
        /**
         * Creates a new subtype.
         * 
         * @param typeName The name of the type
         * @param baseType The base type
         */
        public SubType(String typeName, PhylogeneticType baseType) {
            super(typeName);
            this.baseType = baseType;
        }
        
        /**
         * Gets the base type.
         * 
         * @return The base type
         */
        public PhylogeneticType getBaseType() {
            return baseType;
        }
        
        @Override
        public boolean isAssignableFrom(PhyloSpecType otherType) {
            if (otherType == this) {
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Parameterized phylogenetic type.
     */
    private static class ParameterizedType extends PhylogeneticType {
        private final PhylogeneticType[] parameterTypes;
        
        /**
         * Creates a new parameterized type.
         * 
         * @param typeName The name of the type
         * @param parameterTypes The parameter types
         */
        public ParameterizedType(String typeName, PhylogeneticType... parameterTypes) {
            super(buildTypeName(typeName, parameterTypes));
            this.parameterTypes = parameterTypes;
        }
        
        @Override
        public boolean isParameterized() {
            return true;
        }
        
        @Override
        public boolean isAssignableFrom(PhyloSpecType otherType) {
            if (otherType == this) {
                return true;
            }
            
            if (!(otherType instanceof ParameterizedType)) {
                return false;
            }
            
            ParameterizedType otherParameterizedType = (ParameterizedType) otherType;
            
            // Must have the same base name
            if (!getTypeName().split("<")[0].equals(otherParameterizedType.getTypeName().split("<")[0])) {
                return false;
            }
            
            // Must have the same number of parameter types
            if (parameterTypes.length != otherParameterizedType.parameterTypes.length) {
                return false;
            }
            
            // Each parameter type must be assignable
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(otherParameterizedType.parameterTypes[i])) {
                    return false;
                }
            }
            
            return true;
        }
        
        /**
         * Builds the type name for a parameterized type.
         * 
         * @param typeName The base type name
         * @param parameterTypes The parameter types
         * @return The full type name
         */
        private static String buildTypeName(String typeName, PhylogeneticType[] parameterTypes) {
            StringBuilder builder = new StringBuilder(typeName);
            builder.append('<');
            
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append(parameterTypes[i].getTypeName());
            }
            
            builder.append('>');
            return builder.toString();
        }
    }
}