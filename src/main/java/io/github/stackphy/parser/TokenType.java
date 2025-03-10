package io.github.stackphy.parser;

/**
 * Enumeration of token types in the StackPhy language.
 */
public enum TokenType {
    // Literals
    NUMBER,     // Numeric literal
    STRING,     // String literal
    
    // Special symbols
    BRACKET_OPEN,   // '['
    BRACKET_CLOSE,  // ']'
    
    // Operators
    TILDE,      // '~' (Define stochastic variable)
    EQUAL,      // '=' (Define deterministic variable)
    VAR,        // 'var' (Get variable)
    OBSERVE,    // 'observe' (Attach observed data)
    
    // Base operations
    DUP,        // Duplicate top item
    SWAP,       // Swap top two items
    DROP,       // Remove top item
    
    // Distribution operations
    NORMAL,     // Normal distribution
    LOGNORMAL,  // LogNormal distribution
    EXPONENTIAL, // Exponential distribution
    DIRICHLET,  // Dirichlet distribution
    GAMMA,      // Gamma distribution
    YULE,       // Yule process
    BIRTH_DEATH, // Birth-death process
    COALESCENT, // Coalescent process
    
    // Model operations
    HKY,        // HKY substitution model
    GTR,        // GTR substitution model
    PHYLO_CTMC, // Phylogenetic CTMC
    
    // Data operations
    SEQUENCE,   // Create sequence
    
    // End of file
    EOF,        // End of file
    
    // Error
    ERROR       // Invalid token
}
