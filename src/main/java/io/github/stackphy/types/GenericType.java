package io.github.stackphy.types;

/**
 * A generic type in the PhyloSpec type system.
 * Used for type parameters like T in functions or types.
 */
public class GenericType extends PhyloSpecType {
    
    /**
     * Creates a new generic type with the given name.
     * 
     * @param typeName The name of the type
     */
    public GenericType(String typeName) {
        super(typeName);
    }
    
    @Override
    public boolean isAssignableFrom(PhyloSpecType otherType) {
        // Generic types can be assigned from any type
        return true;
    }
}