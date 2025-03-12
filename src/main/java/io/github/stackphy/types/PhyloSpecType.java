package io.github.stackphy.types;

import java.util.Objects;

/**
 * Base class for all PhyloSpec types.
 */
public abstract class PhyloSpecType {
    private final String typeName;
    
    /**
     * Creates a new PhyloSpec type with the given name.
     * 
     * @param typeName The name of the type
     */
    protected PhyloSpecType(String typeName) {
        this.typeName = typeName;
    }
    
    /**
     * Gets the name of the type.
     * 
     * @return The type name
     */
    public String getTypeName() {
        return typeName;
    }
    
    /**
     * Checks if this type is assignable from another type.
     * 
     * @param otherType The other type to check
     * @return true if this type is assignable from the other type, false otherwise
     */
    public abstract boolean isAssignableFrom(PhyloSpecType otherType);
    
    /**
     * Checks if this type is parameterized.
     * 
     * @return true if this type is parameterized, false otherwise
     */
    public boolean isParameterized() {
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhyloSpecType that = (PhyloSpecType) o;
        return Objects.equals(typeName, that.typeName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(typeName);
    }
    
    @Override
    public String toString() {
        return typeName;
    }
}