package io.github.stackphy.types;

import java.util.Arrays;
import java.util.Objects;

/**
 * Collection types in the PhyloSpec type system.
 */
public class CollectionType extends PhyloSpecType {
    private final String collectionKind;
    private final PhyloSpecType[] elementTypes;
    
    /**
     * Creates a new collection type.
     * 
     * @param collectionKind The kind of collection (Vector, Matrix, Map, Set)
     * @param elementTypes The types of elements in the collection
     */
    public CollectionType(String collectionKind, PhyloSpecType... elementTypes) {
        super(buildTypeName(collectionKind, elementTypes));
        this.collectionKind = collectionKind;
        this.elementTypes = elementTypes;
    }
    
    /**
     * Gets the collection kind.
     * 
     * @return The collection kind
     */
    public String getCollectionKind() {
        return collectionKind;
    }
    
    /**
     * Gets the element types.
     * 
     * @return The element types
     */
    public PhyloSpecType[] getElementTypes() {
        return elementTypes;
    }
    
    @Override
    public boolean isParameterized() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(PhyloSpecType otherType) {
        if (!(otherType instanceof CollectionType)) {
            return false;
        }
        
        CollectionType otherCollectionType = (CollectionType) otherType;
        
        // Must be the same collection kind
        if (!collectionKind.equals(otherCollectionType.collectionKind)) {
            return false;
        }
        
        // Must have the same number of element types
        if (elementTypes.length != otherCollectionType.elementTypes.length) {
            return false;
        }
        
        // Each element type must be assignable
        for (int i = 0; i < elementTypes.length; i++) {
            if (!elementTypes[i].isAssignableFrom(otherCollectionType.elementTypes[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates if a collection conforms to this type's constraints.
     * 
     * @param collection The collection to validate
     * @return true if valid, false otherwise
     */
    public boolean validate(Object collection) {
        // Validation would depend on the specific collection implementation
        // This is a simplified placeholder
        return collection != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CollectionType that = (CollectionType) o;
        return Objects.equals(collectionKind, that.collectionKind) &&
               Arrays.equals(elementTypes, that.elementTypes);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), collectionKind);
        result = 31 * result + Arrays.hashCode(elementTypes);
        return result;
    }
    
    /**
     * Creates a vector type with the given element type.
     * 
     * @param elementType The element type
     * @return The vector type
     */
    public static CollectionType vectorOf(PhyloSpecType elementType) {
        return new CollectionType("Vector", elementType);
    }
    
    /**
     * Creates a matrix type with the given element type.
     * 
     * @param elementType The element type
     * @return The matrix type
     */
    public static CollectionType matrixOf(PhyloSpecType elementType) {
        return new CollectionType("Matrix", elementType);
    }
    
    /**
     * Creates a map type with the given key and value types.
     * 
     * @param keyType The key type
     * @param valueType The value type
     * @return The map type
     */
    public static CollectionType mapOf(PhyloSpecType keyType, PhyloSpecType valueType) {
        return new CollectionType("Map", keyType, valueType);
    }
    
    /**
     * Creates a set type with the given element type.
     * 
     * @param elementType The element type
     * @return The set type
     */
    public static CollectionType setOf(PhyloSpecType elementType) {
        return new CollectionType("Set", elementType);
    }
    
    /**
     * Builds the type name for a collection type.
     * 
     * @param collectionKind The collection kind
     * @param elementTypes The element types
     * @return The type name
     */
    private static String buildTypeName(String collectionKind, PhyloSpecType[] elementTypes) {
        StringBuilder builder = new StringBuilder(collectionKind);
        builder.append('<');
        
        for (int i = 0; i < elementTypes.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(elementTypes[i].getTypeName());
        }
        
        builder.append('>');
        return builder.toString();
    }
    
    // Common type aliases
    public static final CollectionType REAL_VECTOR = vectorOf(PrimitiveType.REAL);
    public static final CollectionType INT_VECTOR = vectorOf(PrimitiveType.INTEGER);
    public static final CollectionType STRING_VECTOR = vectorOf(PrimitiveType.STRING);
    public static final CollectionType REAL_MATRIX = matrixOf(PrimitiveType.REAL);
    
    // Specialized types
    public static final CollectionType SIMPLEX = new SpecializedCollectionType(
            "Simplex", REAL_VECTOR);
    public static final CollectionType Q_MATRIX = new SpecializedCollectionType(
            "QMatrix", REAL_MATRIX);
    
    /**
     * Specialized collection type with a base collection type.
     */
    private static class SpecializedCollectionType extends CollectionType {
        private final CollectionType baseType;
        
        /**
         * Creates a new specialized collection type.
         * 
         * @param typeName The type name
         * @param baseType The base collection type
         */
        public SpecializedCollectionType(String typeName, CollectionType baseType) {
            super(typeName, baseType.getElementTypes());
            this.baseType = baseType;
        }
        
        @Override
        public boolean isAssignableFrom(PhyloSpecType otherType) {
            if (otherType == this) {
                return true;
            }
            
            return false;
        }
    }
}