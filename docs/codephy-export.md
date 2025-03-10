# CodePhy Export

StackPhy includes functionality to export models to the CodePhy JSON format, a standardized format for representing phylogenetic models.

## Using the Exporter

### Command-Line Export

You can export a StackPhy model file to CodePhy JSON using the `stackphy-export` command:

```bash
mvn exec:java@export -Dexec.args="docs/examples/example-hky-yule.sp"
```

This will create a file called `example-hky-yule.json` in the same directory.

You can also specify the output file name:

```bash
mvn exec:java@export -Dexec.args="docs/examples/example-hky-yule.sp output.json"
```

### Programmatic Export

You can also use the `CodePhyExporter` class directly in your code:

```java
// Parse the StackPhy file
StackPhyParser parser = new StackPhyParser();
Environment environment = parser.parseFile(new File("model.sp"));

// Export to CodePhy JSON
CodePhyExporter exporter = new CodePhyExporter(environment);
String json = exporter.exportToJson();

// Use the JSON as needed
System.out.println(json);
```

## CodePhy Format

The CodePhy format is a standardized JSON schema for representing phylogenetic models. It includes:

- Metadata about the model
- Random variables with their distributions
- Deterministic functions
- Constraints
- Observed data

The full schema is available in the `codephy-schema.json` file.

## Example

Here's an example of how a StackPhy model is exported to CodePhy JSON:

### StackPhy Model (example-hky-yule.sp)

```
// Define kappa with log-normal prior
1.0 0.5 lognormal "kappa" ~

// Define nucleotide frequencies with Dirichlet prior
[ 1.0 1.0 1.0 1.0 ] dirichlet "freqs" ~

// Create HKY substitution model
"kappa" var "freqs" var hky "subst_model" =

// Define birth rate for Yule process
0.1 exponential "birth_rate" ~

// Create Yule tree prior
"birth_rate" var yule "tree" ~

// Create phylogenetic CTMC model
"tree" var "subst_model" var phyloCTMC "seq" ~

// Attach observed sequence data
[ 
  "human" "ACGTACGTACGTACGTACGTACGT" sequence 
  "chimp" "ACGTACGTACGTACGTATGTACGT" sequence
  "gorilla" "ACGTACGTACGCACGTACGTACGT" sequence
] "seq" observe
```

### CodePhy JSON

```json
{
  "codephyVersion": "0.1",
  "model": "StackPhy Export",
  "metadata": {
    "title": "Model exported from StackPhy",
    "description": "This model was automatically exported from a StackPhy script.",
    "created": "2025-03-11T12:34:56+00:00",
    "modified": "2025-03-11T12:34:56+00:00",
    "software": {
      "name": "StackPhy",
      "version": "0.1",
      "url": "https://github.com/yourusername/stackphy"
    }
  },
  "randomVariables": {
    "kappa": {
      "distribution": {
        "type": "LogNormal",
        "generates": "REAL",
        "parameters": {
          "meanlog": 1.0,
          "sdlog": 0.5
        }
      }
    },
    "freqs": {
      "distribution": {
        "type": "Dirichlet",
        "generates": "REAL_VECTOR",
        "parameters": {
          "alpha": [1.0, 1.0, 1.0, 1.0]
        }
      }
    },
    "birth_rate": {
      "distribution": {
        "type": "Exponential",
        "generates": "REAL",
        "parameters": {
          "rate": 0.1
        }
      }
    },
    "tree": {
      "distribution": {
        "type": "Yule",
        "generates": "TREE",
        "parameters": {
          "birthRate": {
            "variable": "birth_rate"
          }
        }
      }
    },
    "seq": {
      "distribution": {
        "type": "PhyloCTMC",
        "generates": "ALIGNMENT",
        "parameters": {
          "tree": {
            "variable": "tree"
          },
          "Q": {
            "variable": "subst_model"
          }
        }
      },
      "observedValue": {
        "human": "ACGTACGTACGTACGTACGTACGT",
        "chimp": "ACGTACGTACGTACGTATGTACGT",
        "gorilla": "ACGTACGTACGCACGTACGTACGT"
      }
    }
  },
  "deterministicFunctions": {
    "subst_model": {
      "function": "hky",
      "arguments": {
        "kappa": {
          "variable": "kappa"
        },
        "frequencies": {
          "variable": "freqs"
        }
      }
    }
  }
}
```

## Benefits of CodePhy Export

- **Interoperability**: Models can be shared with other tools that support the CodePhy format
- **Standardization**: Provides a consistent way to represent phylogenetic models
- **Documentation**: The JSON format is self-documenting and easier to read for those not familiar with StackPhy
- **Visualization**: Many tools exist for visualizing JSON data structures
