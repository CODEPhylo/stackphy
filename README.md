# StackPhy

StackPhy is a stack-based language for phylogenetic modeling. It provides a concise, expressive way to define probabilistic models for phylogenetic inference.

## Overview

StackPhy uses a stack-based paradigm for model construction. The language is designed to be:

- **Concise**: Models can be expressed in a minimal amount of code
- **Explicit**: Stack operations make data flow explicit
- **Expressive**: Support for common phylogenetic model components
- **Composable**: Easy to combine different model components

## Quick Start

```
// Define transition/transversion ratio prior
1.0 0.5 lognormal "kappa" ~

// Define nucleotide frequency prior
[ 1.0 1.0 1.0 1.0 ] dirichlet "freqs" ~

// Create HKY substitution model
"kappa" var "freqs" var hky "subst_model" =

// Define birth rate and create Yule tree prior
0.1 exponential "birth_rate" ~
"birth_rate" var yule "tree" ~

// Create phylogenetic CTMC model
"tree" var "subst_model" var phyloCTMC "seq" ~

// Attach observed sequence data
[ "human" "ACGTTGCA..." sequence "chimp" "ACGTTGCA..." sequence ] "seq" observe
```

## Installation

### Prerequisites
- Java 11 or higher
- Maven (optional, you can use the Maven wrapper)

### Building from source
```bash
git clone https://github.com/yourusername/stackphy.git
cd stackphy
mvn package
```

## Running StackPhy

```bash
mvn exec:java -Dexec.args="model.sp"
```

## Documentation

Full documentation is available in the `docs/` directory:

- [Language Definition](LANGUAGE.md)
- [Quick Start Guide](docs/quickstart.md)
- [Tutorial](docs/tutorial.md)
- [Operations Reference](docs/operations.md)
- [Examples](docs/examples/)

## Features

- **Substitution Models**: HKY, GTR
- **Tree Priors**: Yule, Birth-Death, Coalescent
- **Distributions**: Normal, LogNormal, Exponential, Gamma, Dirichlet
- **Sequence Operations**: DNA/RNA/Protein sequence handling

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.