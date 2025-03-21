package io.github.stackphy.model;

/**
 * Types of items that can be on the stack.
 */
public enum StackItemType {
    PRIMITIVE,      // Basic types (number, string, etc.)
    DISTRIBUTION,   // Probability distribution
    VARIABLE,       // Named variable
    SEQUENCE,       // Biological sequence
    MODEL,          // Substitution model or other model
    CONSTRAINT,     // Model constraint
    PARAMETER,      // Parameter (can be a variable, distribution, or primitive)
    FUNCTION        // User-defined function definition
}
