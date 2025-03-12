package io.github.stackphy.parser;

/**
 * Enumeration of token types in the PhyloSpec-compatible StackPhy language.
 */
public enum TokenType {
    // Literals
    NUMBER,     // Numeric literal
    STRING,     // String literal
    IDENTIFIER, // Variable or function identifier
    
    // Special symbols
    BRACKET_OPEN,   // '['
    BRACKET_CLOSE,  // ']'
    PAREN_OPEN,     // '('
    PAREN_CLOSE,    // ')'
    COMMA,          // ','
    COLON,          // ':'
    
    // Operators
    TILDE,      // '~' (Define stochastic variable)
    EQUAL,      // '=' (Define deterministic variable)
    VAR,        // 'var' (Get variable)
    OBSERVE,    // 'observe' (Attach observed data)
    
    // Base operations
    DUP,        // Duplicate top item
    SWAP,       // Swap top two items
    DROP,       // Remove top item
    
    // Constraint operations
    CONSTRAINT, // 'constraint' keyword
    LESS_THAN,  // 'lessThan' constraint
    GREATER_THAN, // 'greaterThan' constraint
    EQUALS,     // 'equals' constraint
    BOUNDED,    // 'bounded' constraint
    SUM_TO,     // 'sumTo' constraint
    MONOPHYLY,  // 'monophyly' constraint
    CALIBRATION, // 'calibration' constraint
    
    // Distribution operations
    NORMAL,     // Normal distribution
    LOGNORMAL,  // LogNormal distribution
    EXPONENTIAL, // Exponential distribution
    GAMMA,      // Gamma distribution
    BETA,       // Beta distribution
    DIRICHLET,  // Dirichlet distribution
    UNIFORM,    // Uniform distribution
    
    // Tree distributions
    YULE,       // Yule process
    BIRTH_DEATH, // Birth-death process
    COALESCENT, // Coalescent process
    FOSSIL_BIRTH_DEATH, // Fossil Birth-death process
    
    // Substitution model operations
    JC69,       // Jukes-Cantor model
    K80,        // Kimura 2-parameter model
    F81,        // Felsenstein 81 model
    HKY,        // HKY substitution model
    GTR,        // GTR substitution model
    WAG,        // WAG protein model
    JTT,        // JTT protein model
    LG,         // LG protein model
    GY94,       // Goldman-Yang codon model
    
    // Rate heterogeneity operations
    DISCRETE_GAMMA, // Discrete gamma distribution
    FREE_RATES,     // Freely varying rates
    INVARIANT_SITES, // Invariant sites model
    STRICT_CLOCK,    // Strict molecular clock
    RELAXED_LOGNORMAL, // Uncorrelated lognormal relaxed clock
    RELAXED_EXPONENTIAL, // Uncorrelated exponential relaxed clock
    
    // Tree operations
    MRCA,           // Most recent common ancestor
    TREE_HEIGHT,    // Height of tree
    NODE_AGE,       // Age of node
    BRANCH_LENGTH,  // Length of branch
    DISTANCE_MATRIX, // Compute distance matrix
    DESCENDANT_TAXA, // Get descendant taxa
    
    // Phylogenetic process operations
    PHYLO_CTMC,     // Phylogenetic CTMC
    PHYLO_BM,       // Phylogenetic Brownian motion
    PHYLO_OU,       // Phylogenetic Ornstein-Uhlenbeck
    
    // Mixture models
    MIXTURE,        // Mixture of distributions
    DISCRETE_GAMMA_MIXTURE, // Discretized gamma mixture
    
    // Mathematical operations
    VECTOR_ELEMENT, // Extract vector element
    MATRIX_ELEMENT, // Extract matrix element
    SCALE,          // Scale vector by factor
    NORMALIZE,      // Normalize vector to simplex
    LOG,            // Natural logarithm
    EXP,            // Exponential function
    SUM,            // Sum of vector elements
    PRODUCT,        // Product of vector elements
    
    // Sequence operations
    SEQUENCE,       // Create sequence
    ALIGNMENT,      // Create alignment
    
    // End of file
    EOF,            // End of file
    
    // Error
    ERROR           // Invalid token
}