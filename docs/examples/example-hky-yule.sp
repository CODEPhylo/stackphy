// Simple HKY + Yule model example in StackPhy (PhyloSpec-aligned)

// Define kappa (transition/transversion ratio) with log-normal prior
1.0     // Mean (on log scale)
0.5     // Standard deviation (on log scale)
LogNormal
"kappa" // Variable name
~       // Define stochastic variable

// Define nucleotide frequencies with Dirichlet prior
[       // Start array
1.0     // Concentration parameter for A
1.0     // Concentration parameter for C
1.0     // Concentration parameter for G
1.0     // Concentration parameter for T
]       // End array
Dirichlet
"baseFreqs" // Variable name (renamed from freqs)
~       // Define stochastic variable

// Create HKY substitution model
"kappa" // Get kappa parameter
var     // Push kappa onto stack
"baseFreqs" // Get frequencies parameter
var     // Push frequencies onto stack
HKY     // Create HKY model (capitalized)
"substModel" // Variable name (camelCase)
=       // Define deterministic variable

// Define birth rate with exponential prior
10.0    // Rate parameter (changed from mean=0.1 to rate=10.0)
Exponential
"birthRate" // Variable name (camelCase)
~       // Define stochastic variable

// Create Yule tree prior
"birthRate" // Get birth rate parameter
var     // Push birth rate onto stack
Yule    // Create Yule process (capitalized)
"phylogeny"  // Variable name (changed from tree)
~       // Define stochastic variable

// Create rate variation across sites
0.5     // Shape parameter
4       // Number of categories
DiscreteGamma
"siteRates" // Variable name
=       // Define deterministic variable

// Create the PhyloCTMC model
"phylogeny"  // Get tree parameter
var     // Push tree onto stack
"substModel" // Get substitution model
var     // Push substitution model onto stack
"siteRates"  // Get site rates
var     // Push site rates onto stack
PhyloCTMC    // Create PhyloCTMC model (capitalized)
"sequences"   // Variable name (changed from seq)
~       // Define stochastic variable

// Observed sequence data
[       // Start array
"human"     "ACGTACGTACGTACGTACGTACGT" // Human sequence
sequence
"chimp"     "ACGTACGTACGTACGTATGTACGT" // Chimp sequence
sequence
"gorilla"   "ACGTACGTACGCACGTACGTACGT" // Gorilla sequence
sequence
]       // End array
"sequences"   // Variable to observe
observe // Attach observed data to the sequence variable

// Add constraint on birth rate 
"birthRate"  // Get birth rate parameter
var     // Push birth rate onto stack
10.0    // Upper bound
LessThan // Create constraint
"birthRateConstraint" // Constraint name
=       // Define constraint