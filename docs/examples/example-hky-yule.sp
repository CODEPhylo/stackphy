// Simple HKY + Yule model example in StackPhy

// Define kappa (transition/transversion ratio) with log-normal prior
1.0     // Mean (on log scale)
0.5     // Standard deviation (on log scale)
lognormal
"kappa" // Variable name
~       // Define stochastic variable

// Define nucleotide frequencies with Dirichlet prior
[       // Start array
1.0     // Concentration parameter for A
1.0     // Concentration parameter for C
1.0     // Concentration parameter for G
1.0     // Concentration parameter for T
]       // End array
dirichlet
"freqs" // Variable name
~       // Define stochastic variable

// Create HKY substitution model
"kappa" // Get kappa parameter
var     // Push kappa onto stack
"freqs" // Get frequencies parameter
var     // Push frequencies onto stack
hky     // Create HKY model
"subst_model" // Variable name
=       // Define deterministic variable

// Define birth rate with exponential prior
0.1     // Rate parameter
exponential
"birth_rate" // Variable name
~       // Define stochastic variable

// Create Yule tree prior
"birth_rate" // Get birth rate parameter
var     // Push birth rate onto stack
yule    // Create Yule process
"tree"  // Variable name
~       // Define stochastic variable

// Create the PhyloCTMC model
"tree"  // Get tree parameter
var     // Push tree onto stack
"subst_model" // Get substitution model
var     // Push substitution model onto stack
phyloCTMC
"seq"   // Variable name
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
"seq"   // Variable to observe
observe // Attach observed data to the sequence variable
