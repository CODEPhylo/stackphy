# StackPhy Language Definition

StackPhy is a stack-based language for building probabilistic phylogenetic models. This document provides a formal definition of the language syntax and semantics.

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

- **KEYWORDS**: Operation names like `normal`, `hky`, `sequence`, etc.

### Comments

Line comments start with `//` and continue to the end of the line:

```
// This is a comment
1.0 // This is an inline comment
```

### Whitespace

Whitespace (spaces, tabs, newlines) is ignored except as a token separator.

## Data Types

All values in StackPhy are typed. The following types exist:

- **Parameter**: Represents a node in the model graph
  - **Primitive**: Anonymous parameter (number, string, array)
  - **Variable**: Named parameter that can be stochastic or deterministic

- **Distribution**: Probability distribution for stochastic variables
  - Basic distributions: `normal`, `lognormal`, `exponential`, `dirichlet`, `gamma`
  - Tree priors: `yule`, `birthDeath`, `coalescent`
  - Sequence evolution: `phyloCTMC`

- **Model**: Substitution model for sequence evolution
  - `hky`: Hasegawa-Kishino-Yano model
  - `gtr`: General Time Reversible model

- **Sequence**: DNA, RNA, or protein sequence data

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

### Variable Operations

```
~      # Defines a stochastic variable (Distribution, String → )
=      # Defines a deterministic variable (StackItem, String → )
var    # Gets a variable by name (String → Variable)
observe # Attaches observed data to a variable (StackItem, String → )
```

### Distribution Operations

```
normal      # Creates a normal distribution (Parameter, Parameter → Distribution)
lognormal   # Creates a log-normal distribution (Parameter, Parameter → Distribution)
exponential # Creates an exponential distribution (Parameter → Distribution)
dirichlet   # Creates a Dirichlet distribution (Parameter → Distribution)
gamma       # Creates a gamma distribution (Parameter, Parameter → Distribution)
yule        # Creates a Yule process tree prior (Parameter → Distribution)
birthDeath  # Creates a birth-death process tree prior (Parameter, Parameter → Distribution)
coalescent  # Creates a coalescent process tree prior (Parameter → Distribution)
phyloCTMC   # Creates a phylogenetic CTMC (Parameter, Parameter → Distribution)
```

### Model Operations

```
hky    # Creates an HKY substitution model (Parameter, Parameter → Model)
gtr    # Creates a GTR substitution model (Parameter, Parameter → Model)
```

### Data Operations

```
sequence    # Creates a sequence object (String, String → Sequence)
```

## Stack Manipulation

Operations follow a classic stack-based approach, where parameters are popped from the stack in reverse order. For example, the `normal` operation:

```
1.0  # Push mean onto stack
0.5  # Push standard deviation onto stack
normal  # Pop standard deviation, pop mean, push normal distribution
```

## Variables and References

Variables are named parameters in the model graph:

- **Stochastic variables** are defined with the `~` operator
- **Deterministic variables** are defined with the `=` operator
- Variables are referenced using the `var` operator

Example:
```
1.0 0.5 normal "x" ~  # Define stochastic variable x
"x" var              # Push variable x onto stack
```

## Complete Example

Here's a complete example of an HKY model with a Yule tree prior:

```
// Define kappa (transition/transversion ratio)
1.0 0.5 lognormal "kappa" ~

// Define nucleotide frequencies
[ 1.0 1.0 1.0 1.0 ] dirichlet "freqs" ~

// Create HKY substitution model
"kappa" var "freqs" var hky "subst_model" =

// Define birth rate
0.1 exponential "birth_rate" ~

// Create Yule tree prior
"birth_rate" var yule "tree" ~

// Create the PhyloCTMC model
"tree" var "subst_model" var phyloCTMC "seq" ~

// Attach observed sequence data
[ "human" "ACGTACGT" sequence "chimp" "ACGTACGC" sequence ] "seq" observe
```

## Formal Grammar

```
program: statement*
statement: value | operation
value: NUMBER | STRING
operation: KEYWORD | OPERATOR | BRACKET_OPEN | BRACKET_CLOSE

KEYWORD: "dup" | "swap" | "drop" | "normal" | "lognormal" | "exponential" |
         "dirichlet" | "gamma" | "yule" | "birthDeath" | "coalescent" |
         "phyloCTMC" | "hky" | "gtr" | "sequence" | "var"
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

## Type System

The type system ensures that operations receive the correct types of parameters:

- **Distribution operations** expect specific parameter types
  - `normal` and `lognormal` expect two numeric parameters: mean and standard deviation
  - `exponential` expects one numeric parameter: rate
  - `dirichlet` expects an array parameter: concentration parameters
  - etc.

- **Model operations** expect specific parameter types
  - `hky` expects a kappa parameter and an array of base frequencies
  - `gtr` expects an array of rate parameters and an array of base frequencies

- **Variable operations** expect specific types
  - `~` expects a distribution and a string
  - `=` expects any stack item and a string
  - `var` expects a string
  - `observe` expects a stack item and a string

Type checking is performed during execution, and appropriate error messages are generated for type mismatches.
