package io.github.stackphy.types;

/**
 * Primitive types in the PhyloSpec type system.
 */
public class PrimitiveType extends PhyloSpecType {
    // Standard primitive types
    public static final PrimitiveType REAL = new PrimitiveType("Real");
    public static final PrimitiveType INTEGER = new PrimitiveType("Integer");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("Boolean");
    public static final PrimitiveType STRING = new PrimitiveType("String");
    
    // Restricted types
    public static final PrimitiveType POSITIVE_REAL = new RestrictedType("PositiveReal", REAL);
    public static final PrimitiveType PROBABILITY = new RestrictedType("Probability", REAL);
    public static final PrimitiveType NON_NEG_REAL = new RestrictedType("NonNegReal", REAL);
    public static final PrimitiveType POS_INTEGER = new RestrictedType("PosInteger", INTEGER);
    
    /**
     * Creates a new primitive type with the given name.
     * 
     * @param typeName The name of the type
     */
    protected PrimitiveType(String typeName) {
        super(typeName);
    }
    
    @Override
    public boolean isAssignableFrom(PhyloSpecType otherType) {
        if (otherType == this) {
            return true;
        }
        
        // Special assignability rules
        if (this == REAL) {
            return otherType == INTEGER || otherType == POSITIVE_REAL || 
                   otherType == PROBABILITY || otherType == NON_NEG_REAL;
        }
        
        if (this == NON_NEG_REAL) {
            return otherType == POSITIVE_REAL || otherType == PROBABILITY || 
                   otherType == POS_INTEGER;
        }
        
        if (otherType instanceof RestrictedType) {
            RestrictedType restrictedType = (RestrictedType) otherType;
            return this.isAssignableFrom(restrictedType.getBaseType());
        }
        
        return false;
    }
    
    /**
     * Validates if a value conforms to this type's constraints.
     * 
     * @param value The value to validate
     * @return true if the value is valid for this type, false otherwise
     */
    public boolean validate(Object value) {
        if (this == REAL || this == NON_NEG_REAL || this == POSITIVE_REAL || this == PROBABILITY) {
            return value instanceof Number;
        } else if (this == INTEGER || this == POS_INTEGER) {
            return value instanceof Integer || value instanceof Long;
        } else if (this == BOOLEAN) {
            return value instanceof Boolean;
        } else if (this == STRING) {
            return value instanceof String;
        }
        return false;
    }
    
    /**
     * Creates a type based on a value's Java class.
     * 
     * @param value The value to infer type from
     * @return The corresponding PhyloSpec type, or null if no matching type
     */
    public static PrimitiveType fromValue(Object value) {
        if (value instanceof Double || value instanceof Float) {
            double doubleValue = ((Number) value).doubleValue();
            if (doubleValue > 0) {
                return POSITIVE_REAL;
            } else if (doubleValue >= 0) {
                return NON_NEG_REAL;
            } else {
                return REAL;
            }
        } else if (value instanceof Integer || value instanceof Long) {
            long longValue = ((Number) value).longValue();
            if (longValue > 0) {
                return POS_INTEGER;
            } else {
                return INTEGER;
            }
        } else if (value instanceof Boolean) {
            return BOOLEAN;
        } else if (value instanceof String) {
            return STRING;
        }
        return null;
    }
    
    /**
     * Restricted primitive type with a base type.
     */
    private static class RestrictedType extends PrimitiveType {
        private final PrimitiveType baseType;
        
        /**
         * Creates a new restricted type.
         * 
         * @param typeName The name of the type
         * @param baseType The base type
         */
        public RestrictedType(String typeName, PrimitiveType baseType) {
            super(typeName);
            this.baseType = baseType;
        }
        
        /**
         * Gets the base type.
         * 
         * @return The base type
         */
        public PrimitiveType getBaseType() {
            return baseType;
        }
        
        @Override
        public boolean isAssignableFrom(PhyloSpecType otherType) {
            if (otherType == this) {
                return true;
            }
            
            // Handle special cases for restricted types
            if (this == POSITIVE_REAL) {
                return otherType == POS_INTEGER;
            } else if (this == NON_NEG_REAL) {
                return otherType == POSITIVE_REAL || otherType == PROBABILITY || 
                       otherType == POS_INTEGER;
            }
            
            return false;
        }
        
        @Override
        public boolean validate(Object value) {
            if (!baseType.validate(value)) {
                return false;
            }
            
            if (this == POSITIVE_REAL) {
                return ((Number) value).doubleValue() > 0;
            } else if (this == NON_NEG_REAL) {
                return ((Number) value).doubleValue() >= 0;
            } else if (this == PROBABILITY) {
                double doubleValue = ((Number) value).doubleValue();
                return doubleValue >= 0 && doubleValue <= 1;
            } else if (this == POS_INTEGER) {
                return ((Number) value).longValue() > 0;
            }
            
            return true;
        }
    }
}