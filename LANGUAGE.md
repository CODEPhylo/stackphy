# StackPhy Language Definition (PhyloSpec-aligned)

StackPhy is a stack-based language for building probabilistic phylogenetic models. This document provides a formal definition of the language syntax and semantics, aligned with the PhyloSpec standard.

## Overview

StackPhy uses a stack-based paradigm for model construction. Operations consume values from the stack and push results back onto it. The language is designed to define hierarchical probabilistic models for phylogenetic inference.

## Lexical Elements

### Tokens

StackPhy recognizes the following token types:

- **NUMBER**: Numeric literals (`[+-]?([0-9]*[.])?[0-9]+`)
  - Examples: `1`, `2.5`, `-0.1`

- **STRING**: String literals enclosed in double quotes (`"([^"\\]|\\.)*"`)
  - Examples: `"kappa"`, `"ACGTACGT"`, `"human"`

- **BRACKET_OPEN**: Opening bracket for array construction (`[`)

- **BRACKET_CLOSE**: Closing bracket for array construction (`]`)

- **OPERATOR**: Special operators:
  - `~` (Define stochastic variable)
  - `=` (Define deterministic variable)
  - `var` (Get variable)
  - `observe` (Attach observed data)

- **KEYWORDS**: Operation names like `Normal`, `HKY`, `sequence`, etc.

### Comments

Line comments start with `//` and continue to the end of the line:

```
// This is a comment
1.0 // This is an inline comment
```

### Whitespace

Whitespace (spaces, tabs, newlines) is ignored except as a token separator.

## Type System

StackPhy implements the PhyloSpec type system:

### Basic Types
- `Real`: Real-valued number
- `Integer`: Integer-valued number
- `Boolean`: Logical value (true/false)
- `String`: Text value

### Specialized Types
- `PositiveReal`: Positive real number (> 0)
- `Probability`: Real number between 0 and 1
- `Simplex`: Probability vector that sums to 1

### Collection Types
- `Vector<T>`: Vector of elements of type T
- `Matrix<T>`: Matrix of elements of type T

### Phylogenetic Types
- `Tree`: Phylogenetic tree
- `TimeTree`: Time-calibrated tree
- `QMatrix`: Rate matrix for substitution models
- `Alignment<A>`: Multiple sequence alignment (parameterized by alphabet A)
- `Taxon`: Single taxonomic unit
- `TaxonSet`: Set of taxa

## Execution Model

StackPhy uses a stack-based execution model:

1. The execution begins with an empty stack and environment
2. Tokens are processed sequentially from left to right
3. Numbers and strings are pushed onto the stack
4. Operations pop their inputs from the stack and push their outputs to the stack
5. Variables are stored in the environment
6. After execution, the environment contains the complete model

## Operations

### Base Operations

```
dup    # Duplicates the top stack item
swap   # Swaps the top two stack items
drop   # Removes the top stack item
```

### Array Operations

```
[      # Marks the beginning of array construction
]      # Creates an array from items since the last array marker
```

### Type Operations

```
Real           # Creates a typed Real value (Number → Real)
Integer        # Creates a typed Integer value (Number → Integer)
PositiveReal   # Creates a typed PositiveReal value (Number → PositiveReal)
Probability    # Creates a typed Probability value (Number → Probability)
```

### Variable Operations

```
~      # Defines a stochastic variable (Distribution, String → )
=      # Defines a deterministic variable (StackItem, String → )
var    # Gets a variable by name (String → Variable)
observe # Attaches observed data to a variable (StackItem, String → )
```

### Distribution Operations

```
Normal       # Creates a normal distribution (Real, PositiveReal → Distribution)
LogNormal    # Creates a log-normal distribution (Real, PositiveReal → Distribution)
Exponential  # Creates an exponential distribution (PositiveReal → Distribution)
Dirichlet    # Creates a Dirichlet distribution (Vector<PositiveReal> → Distribution)
Gamma        # Creates a gamma distribution (PositiveReal, PositiveReal → Distribution)
Beta         # Creates a beta distribution (PositiveReal, PositiveReal → Distribution)
Yule         # Creates a Yule process tree prior (PositiveReal → Distribution)
BirthDeath   # Creates a birth-death process tree prior (PositiveReal, PositiveReal → Distribution)
Coalescent   # Creates a coalescent process tree prior (PositiveReal → Distribution)
PhyloCTMC    # Creates a phylogenetic CTMC (Tree, QMatrix → Distribution)
```

### Model Operations

```
HKY    # Creates an HKY substitution model (PositiveReal, Simplex → QMatrix)
GTR    # Creates a GTR substitution model (Vector<PositiveReal>, Simplex → QMatrix)
JC69   # Creates a JC69 substitution model (→ QMatrix)
F81    # Creates an F81 substitution model (Simplex → QMatrix)
WAG    # Creates a WAG protein substitution model (→ QMatrix)
```

### Rate Heterogeneity Operations

```
DiscreteGamma    # Creates a discrete gamma rate distribution (PositiveReal, Integer → Vector<PositiveReal>)
InvariantSites   # Creates an invariant sites model (Probability → Vector<Real>)
```

### Constraint Operations

```
LessThan         # Creates a less than constraint (Real, Real → Constraint)
GreaterThan      # Creates a greater than constraint (Real, Real → Constraint)
Bounded          # Creates a bounded value constraint (Real, Real, Real → Constraint)
Equals           # Creates an equality constraint (Any, Any → Constraint)
```

### Tree Operations

```
MRCA             # Finds the most recent common ancestor (Tree, TaxonSet → TreeNode)
NodeAge          # Gets the age of a node (TreeNode → Real)
TreeHeight       # Gets the height of a tree (Tree → Real)
```

### Data Operations

```
sequence         # Creates a sequence object (String, String → Sequence)
taxon            # Creates a taxon object (String → Taxon)
taxonSet         # Creates a set of taxa (Vector<Taxon> → TaxonSet)
```

## Stack Manipulation

Operations follow a classic stack-based approach, where parameters are popped from the stack in reverse order. For example, the `Normal` operation:

```
1.0  # Push mean onto stack
0.5  # Push standard deviation onto stack
Normal  # Pop standard deviation, pop mean, push normal distribution
```

## Variables and References

Variables are named parameters in the model graph:

- **Stochastic variables** are defined with the `~` operator
- **Deterministic variables** are defined with the `=` operator
- Variables are referenced using the `var` operator

Example:
```
1.0 0.5 Normal "x" ~  # Define stochastic variable x
"x" var              # Push variable x onto stack
```

## Complete Example

Here's a complete example of an HKY model with a Yule tree prior:

```
// Define kappa (transition/transversion ratio)
1.0 0.5 LogNormal "kappa" ~

// Define nucleotide frequencies
[ 1.0 1.0 1.0 1.0 ] Dirichlet "baseFreqs" ~

// Create HKY substitution model
"kappa" var "baseFreqs" var HKY "substModel" =

// Define birth rate
10.0 Exponential "birthRate" ~

// Create Yule tree prior
"birthRate" var Yule "phylogeny" ~

// Create rate variation across sites
0.5 4 DiscreteGamma "siteRates" =

// Create the PhyloCTMC model
"phylogeny" var "substModel" var "siteRates" var PhyloCTMC "sequences" ~

// Attach observed sequence data
[ 
  "human" "ACGTACGT" sequence 
  "chimp" "ACGTACGC" sequence 
] "sequences" observe

// Add constraint on birth rate
"birthRate" var 10.0 LessThan "birthRateConstraint" =
```

## Formal Grammar

```
program: statement*
statement: value | operation
value: NUMBER | STRING
operation: KEYWORD | OPERATOR | BRACKET_OPEN | BRACKET_CLOSE

KEYWORD: "dup" | "swap" | "drop" | "Normal" | "LogNormal" | "Exponential" |
         "Dirichlet" | "Gamma" | "Yule" | "BirthDeath" | "Coalescent" |
         "PhyloCTMC" | "HKY" | "GTR" | "sequence" | "var" | "DiscreteGamma" |
         "LessThan" | "GreaterThan" | "Bounded" | "Real" | "Integer" | 
         "PositiveReal" | "Probability" | "MRCA" | "NodeAge"
OPERATOR: "~" | "=" | "observe"
BRACKET_OPEN: "["
BRACKET_CLOSE: "]"
NUMBER: [+-]?([0-9]*[.])?[0-9]+
STRING: "([^"\\]|\\.)*"
```

## Execution Semantics

1. Initialize empty stack and environment
2. For each token in the program:
   a. If token is a NUMBER, create a Primitive parameter with the numeric value and push it onto the stack
   b. If token is a STRING, create a Primitive parameter with the string value and push it onto the stack
   c. If token is BRACKET_OPEN, push a special marker onto the stack
   d. If token is BRACKET_CLOSE, pop items from the stack until marker, create an array, and push it onto the stack
   e. If token is an operation, execute it according to its definition
3. After execution, the environment contains the complete model

## Type System Rules

The type system ensures that operations receive the correct types of parameters:

- **Distribution operations** expect specific parameter types
  - `Normal` expects a Real (mean) and a PositiveReal (sd)
  - `LogNormal` expects a Real (meanlog) and a PositiveReal (sdlog)
  - `Exponential` expects a PositiveReal (rate)
  - `Dirichlet` expects a Vector<PositiveReal> (alpha)

- **Model operations** expect specific parameter types
  - `HKY` expects a PositiveReal (kappa) and a Simplex (baseFrequencies)
  - `GTR` expects a Vector<PositiveReal> (rateMatrix) and a Simplex (baseFrequencies)

- **Variable operations** expect specific types
  - `~` expects a distribution and a string
  - `=` expects any stack item and a string
  - `var` expects a string
  - `observe` expects a stack item and a string

Type checking is performed during execution, and appropriate error messages are generated for type mismatches.