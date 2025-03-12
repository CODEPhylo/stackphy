# StackPhy

StackPhy is a stack-based language for phylogenetic modeling that conforms to the PhyloSpec reference standard. It provides a concise, expressive way to define probabilistic models for phylogenetic inference.

## Overview

StackPhy uses a stack-based paradigm for model construction. The language is designed to be:

- **Concise**: Models can be expressed in a minimal amount of code
- **Explicit**: Stack operations make data flow explicit
- **Expressive**: Support for common phylogenetic model components
- **Composable**: Easy to combine different model components
- **PhyloSpec-compliant**: Follows the PhyloSpec reference standard

## Quick Start

```
// Define transition/transversion ratio prior
1.0 0.5 LogNormal "kappa" ~

// Define nucleotide frequency prior
[ 1.0 1.0 1.0 1.0 ] Dirichlet "baseFreqs" ~

// Create HKY substitution model
"kappa" var "baseFreqs" var HKY "substModel" =

// Define birth rate and create Yule tree prior
10.0 Exponential "birthRate" ~
"birthRate" var Yule "phylogeny" ~

// Create rate variation across sites
0.5 4 DiscreteGamma "siteRates" =

// Create phylogenetic CTMC model
"phylogeny" var "substModel" var "siteRates" var PhyloCTMC "sequences" ~

// Attach observed sequence data
[ "human" "ACGTTGCA..." sequence "chimp" "ACGTTGCA..." sequence ] "sequences" observe
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
- [PhyloSpec Compliance](docs/phylospec.md)

## Features

- **Type System**: Real, Integer, PositiveReal, Probability, Tree, TimeTree, etc.
- **Substitution Models**: HKY, GTR, JC69, F81
- **Tree Priors**: Yule, BirthDeath, Coalescent
- **Distributions**: Normal, LogNormal, Exponential, Gamma, Dirichlet, Beta
- **Rate Heterogeneity**: DiscreteGamma, InvariantSites
- **Constraints**: LessThan, GreaterThan, Bounded, Equals
- **Sequence Operations**: DNA/RNA/Protein sequence handling

## PhyloSpec Compliance

StackPhy implements the [PhyloSpec](https://github.com/phylospec/phylospec) reference standard, ensuring semantic interoperability with other phylogenetic modeling languages. This means models written in StackPhy can be translated to and from other PhyloSpec-compliant languages like ModelPhy and CodePhy.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.