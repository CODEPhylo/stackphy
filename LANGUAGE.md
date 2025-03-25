# StackPhy Language Definition

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

- **IDENTIFIER**: Identifiers for user-defined functions and variables (`[a-zA-Z_][a-zA-Z0-9_]*`)

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
6. User-defined functions are invoked by name
7. After execution, the environment contains the complete model

## Operations

### Base Operations

```
dup    # Duplicates the top stack item
swap   # Swaps the top two stack items
drop   # Removes the top stack item
over   # Copies the second stack item to the top
rot    # Rotates the top three stack items
nip    # Removes the second stack item
tuck   # Copies the top stack item to the third position
pick   # Copies the nth stack item to the top
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

### Math Operations

```
+      # Addition (Number, Number → Number)
-      # Subtraction (Number, Number → Number)
*      # Multiplication (Number, Number → Number)
/      # Division (Number, Number → Number)
log    # Natural logarithm (Number → Number)
exp    # Exponential function (Number → Number)
sum    # Sum of array elements (Vector<Number> → Number)
product # Product of array elements (Vector<Number> → Number)
scale  # Scale a vector (Vector<Number>, Number → Vector<Number>)
normalize # Normalize a vector to sum to 1 (Vector<Number> → Vector<Number>)
negate # Negate a number (Number → Number)
sqrt   # Square root (Number → Number)
pi     # Push the value of pi onto the stack (→ Number)
```

### Distribution Operations

```
Normal       # Creates a normal distribution (Real, PositiveReal → Distribution)
LogNormal    # Creates a log-normal distribution (Real, PositiveReal → Distribution)
Exponential  # Creates an exponential distribution (PositiveReal → Distribution)
Dirichlet    # Creates a Dirichlet distribution (Vector<PositiveReal> → Distribution)
Gamma        # Creates a gamma distribution (PositiveReal, PositiveReal → Distribution)
Beta         # Creates a beta distribution (PositiveReal, PositiveReal → Distribution)
Uniform      # Creates a uniform distribution (Real, Real → Distribution)
Yule         # Creates a Yule process tree prior (PositiveReal → Distribution)
BirthDeath   # Creates a birth-death process tree prior (PositiveReal, PositiveReal → Distribution)
Coalescent   # Creates a coalescent process tree prior (PositiveReal → Distribution)
PhyloCTMC    # Creates a phylogenetic CTMC (Tree, QMatrix → Distribution)
PhyloBM      # Creates a phylogenetic Brownian motion model (Tree, Real → Distribution)
PhyloOU      # Creates a phylogenetic Ornstein-Uhlenbeck model (Tree, Real, Real → Distribution)
```

### Model Operations

```
HKY    # Creates an HKY substitution model (PositiveReal, Simplex → QMatrix)
GTR    # Creates a GTR substitution model (Vector<PositiveReal>, Simplex → QMatrix)
JC69   # Creates a JC69 substitution model (→ QMatrix)
F81    # Creates an F81 substitution model (Simplex → QMatrix)
K80    # Creates a K80 substitution model (PositiveReal → QMatrix)
WAG    # Creates a WAG protein substitution model (→ QMatrix)
JTT    # Creates a JTT protein substitution model (→ QMatrix)
LG     # Creates an LG protein substitution model (→ QMatrix)
GY94   # Creates a GY94 codon substitution model (PositiveReal, Simplex → QMatrix)
```

### Rate Heterogeneity Operations

```
DiscreteGamma      # Creates a discrete gamma rate distribution (PositiveReal, Integer → Vector<PositiveReal>)
DiscreteGammaVector # Creates a vector of discrete gamma rates (PositiveReal, Integer → Vector<PositiveReal>)
FreeRates          # Creates a free rates model (Vector<PositiveReal>, Vector<Probability> → Vector<PositiveReal>)
InvariantSites     # Creates an invariant sites model (Probability → Vector<Real>)
StrictClock        # Creates a strict molecular clock model (PositiveReal → Tree → Tree)
UncorrelatedLognormal # Creates an uncorrelated lognormal relaxed clock model (PositiveReal, PositiveReal → Tree → Tree)
UncorrelatedExponential # Creates an uncorrelated exponential relaxed clock model (PositiveReal → Tree → Tree)
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

### User-Defined Functions

StackPhy supports user-defined functions with the following syntax:

```
: functionName ( inputParams -- outputDescription ) 
    // function body
;
```

Where:
- `:` marks the beginning of a function definition
- `functionName` is the name of the function (must be a valid identifier)
- `(inputParams -- outputDescription)` is an optional stack effect declaration
- `inputParams` lists the input parameters consumed from the stack
- `--` separates inputs from outputs
- `outputDescription` describes what the function returns to the stack
- `;` marks the end of the function definition

Example:

```
: negate ( n -- -n ) 
    -1 * 
;

: square ( n -- n² ) 
    dup * 
;
```

Functions are called by name:

```
5 negate    // Pushes -5 onto the stack
3 square    // Pushes 9 onto the stack
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

## Example with User-Defined Functions

Here's an example that uses user-defined functions:

```
// Define a function to create a standard normal distribution
: standardNormal ( -- dist )
    0.0 1.0 Normal
;

// Define a function to create an HKY model with equal base frequencies
: equalBaseFreqHKY ( kappa -- model )
    [ 0.25 0.25 0.25 0.25 ] HKY
;

// Use the functions in a model
standardNormal "x" ~
2.0 equalBaseFreqHKY "substModel" =
```

## Formal Grammar

```
program: statement* EOF
statement: functionDefinition | operation | value | comment
functionDefinition: ':' IDENTIFIER stackEffect? statement* ';'
stackEffect: '(' inputParams STACK_SEPARATOR outputDescription ')'
inputParams: (IDENTIFIER)*
outputDescription: (IDENTIFIER | NUMBER | '*' | '+' | '-' | '/' | '.')*
operation: baseOperation | mathOperation | distributionOperation | arrayOperation | IDENTIFIER
baseOperation: 'dup' | 'swap' | 'drop' | 'over' | 'rot' | 'nip' | 'tuck' | 'pick' | '~' | '=' | 'var' | 'observe'
mathOperation: '*' | '+' | '-' | '/' | 'log' | 'exp' | 'sum' | 'product' | 'scale' | 'normalize' | 'vectorElement' | 'matrixElement' | 'negate' | 'sqrt' | 'pi'
distributionOperation: 'Normal' | 'LogNormal' | 'Exponential' | 'Gamma' | 'Beta' | 'Dirichlet' | 'Uniform' | 'Yule' | 'BirthDeath' | 'Coalescent' | 'FossilBirthDeath' | 'JC69' | 'K80' | 'F81' | 'HKY' | 'GTR' | 'WAG' | 'JTT' | 'LG' | 'GY94' | 'DiscreteGamma' | 'DiscreteGammaVector' | 'FreeRates' | 'InvariantSites' | 'StrictClock' | 'UncorrelatedLognormal' | 'UncorrelatedExponential' | 'PhyloCTMC' | 'PhyloBM' | 'PhyloOU'
arrayOperation: '[' | ']'
value: NUMBER | STRING
comment: '//' ~[\r\n]*
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*
NUMBER: '-'? DIGIT+ ('.' DIGIT*)? | '-'? '.' DIGIT+
STRING: '"' (~["\r\n] | '\\"')* '"'
STACK_SEPARATOR: '--'
```

## Execution Semantics

1. Initialize empty stack and environment
2. For each token in the program:
   a. If token is a NUMBER, create a Primitive parameter with the numeric value and push it onto the stack
   b. If token is a STRING, create a Primitive parameter with the string value and push it onto the stack
   c. If token is BRACKET_OPEN, push a special marker onto the stack
   d. If token is BRACKET_CLOSE, pop items from the stack until marker, create an array, and push it onto the stack
   e. If token is a function name, execute the function
   f. If token is a function definition, define the function in the environment
   g. If token is an operation, execute it according to its definition
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

- **User-defined functions** follow the type signatures specified in their stack effect declarations

Type checking is performed during execution, and appropriate error messages are generated for type mismatches.