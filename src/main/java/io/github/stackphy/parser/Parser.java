package io.github.stackphy.parser;

import io.github.stackphy.distribution.*;
import io.github.stackphy.functions.*;
import io.github.stackphy.model.*;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;
import io.github.stackphy.substitution.*;
import io.github.stackphy.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the StackPhy language that is semantically compatible with PhyloSpec.
 * Converts tokens into operations.
 */
public class Parser {
    private final List<Token> tokens;
    private int position;
    private final Map<String, NamedOperation.OperationExecutor> operations;
    
    /**
     * Creates a new parser for the given tokens.
     * 
     * @param tokens The tokens to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.operations = new HashMap<>();
        registerOperations();
    }
    
    /**
     * Parses the tokens into a list of operations.
     * 
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    public List<Operation> parse() throws StackPhyException {
        List<Operation> operations = new ArrayList<>();
        
        while (!isAtEnd()) {
            Token token = advance();
            Operation operation = null;
            
            switch (token.getType()) {
                case NUMBER:
                    try {
                        // Parse number
                        double value = Double.parseDouble(token.getValue());
                        operation = new ValueOperation(value, token.getLine(), token.getColumn());
                    } catch (NumberFormatException e) {
                        throw new StackPhyException("Invalid number: " + token.getValue(), token.getLine(), token.getColumn());
                    }
                    break;
                
                case STRING:
                    // String literal
                    operation = new ValueOperation(token.getValue(), token.getLine(), token.getColumn());
                    break;
                
                case BRACKET_OPEN:
                    // Array start marker (special string value)
                    operation = new ValueOperation("ARRAY_MARKER", token.getLine(), token.getColumn());
                    break;
                
                case BRACKET_CLOSE:
                case TILDE:
                case EQUAL:
                case VAR:
                case OBSERVE:
                case DUP:
                case SWAP:
                case DROP:
                case NORMAL:
                case LOGNORMAL:
                case EXPONENTIAL:
                case DIRICHLET:
                case GAMMA:
                case BETA:
                case UNIFORM:
                case YULE:
                case BIRTH_DEATH:
                case COALESCENT:
                case FOSSIL_BIRTH_DEATH:
                case JC69:
                case K80:
                case F81:
                case HKY:
                case GTR:
                case WAG:
                case JTT:
                case LG:
                case GY94:
                case DISCRETE_GAMMA:
                case FREE_RATES:
                case INVARIANT_SITES:
                case STRICT_CLOCK:
                case RELAXED_LOGNORMAL:
                case RELAXED_EXPONENTIAL:
                case PHYLO_CTMC:
                case PHYLO_BM:
                case PHYLO_OU:
                case MIXTURE:
                case DISCRETE_GAMMA_MIXTURE:
                case MRCA:
                case TREE_HEIGHT:
                case NODE_AGE:
                case BRANCH_LENGTH:
                case DISTANCE_MATRIX:
                case DESCENDANT_TAXA:
                case VECTOR_ELEMENT:
                case MATRIX_ELEMENT:
                case SCALE:
                case NORMALIZE:
                case LOG:
                case EXP:
                case SUM:
                case PRODUCT:
                case SEQUENCE:
                case ALIGNMENT:
                case LESS_THAN:
                case GREATER_THAN:
                case EQUALS:
                case BOUNDED:
                case SUM_TO:
                case MONOPHYLY:
                case CALIBRATION:
                case CONSTRAINT:
                    // Named operation
                    String name = token.getValue().toLowerCase(); // PhyloSpec operations are case-insensitive
                    if (this.operations.containsKey(name)) {
                        operation = new NamedOperation(name, this.operations.get(name), token.getLine(), token.getColumn());
                    } else {
                        throw new StackPhyException("Unknown operation: " + name, token.getLine(), token.getColumn());
                    }
                    break;
                
                case IDENTIFIER:
                    // For user-defined identifiers
                    throw new StackPhyException("Unexpected identifier: " + token.getValue(), token.getLine(), token.getColumn());
                
                case ERROR:
                    throw new StackPhyException("Syntax error: " + token.getValue(), token.getLine(), token.getColumn());
                
                case EOF:
                    // End of file, stop parsing
                    return operations;
                
                default:
                    throw new StackPhyException("Unexpected token: " + token, token.getLine(), token.getColumn());
            }
            
            operations.add(operation);
        }
        
        return operations;
    }
    
    /**
     * Registers all operations according to PhyloSpec semantics.
     */
    private void registerOperations() {
        // Base operations
        operations.put("dup", (stack, env) -> stack.dup());
        operations.put("swap", (stack, env) -> stack.swap());
        operations.put("drop", (stack, env) -> stack.drop());
        
        // Array operations
        operations.put("]", (stack, env) -> {
            List<Object> elements = new ArrayList<>();
            
            while (!stack.isEmpty()) {
                StackItem item = stack.pop();
                
                if (item instanceof Primitive) {
                    Primitive primitive = (Primitive) item;
                    if (primitive.isString() && "ARRAY_MARKER".equals(primitive.getStringValue())) {
                        break;
                    }
                    // Store the actual value, not the StackItem
                    elements.add(0, primitive.getValue());
                } else {
                    // For non-primitives, store the item itself
                    elements.add(0, item);
                }
            }
            
            // Create a Primitive that wraps the array
            stack.push(new Primitive(elements.toArray()));
        });       
         
        // Variable operations
        operations.put("~", (stack, env) -> {
            // Stack is [Distribution, Name]
            String name = null;
            
            // Pop the variable name (top of stack)
            StackItem nameItem = stack.pop();
            if (nameItem instanceof Primitive) {
                Primitive primitive = (Primitive) nameItem;
                if (primitive.isString()) {
                    name = primitive.getStringValue();
                }
            }
            
            if (name == null) {
                throw new IllegalArgumentException("Expected string for variable name");
            }
            
            // Pop the distribution (now top of stack)
            StackItem value = stack.pop();
            
            if (value.getType() != StackItemType.DISTRIBUTION) {
                throw new IllegalArgumentException("Expected distribution for stochastic variable");
            }
            
            env.defineVariable(name, value, true);
        });

        operations.put("=", (stack, env) -> {
            // First pop the name (on top of stack)
            String name = null;
            StackItem nameItem = stack.pop();
            
            if (nameItem instanceof Primitive) {
                Primitive primitive = (Primitive) nameItem;
                if (primitive.isString()) {
                    name = primitive.getStringValue();
                }
            }
            
            if (name == null) {
                throw new IllegalArgumentException("Expected string for variable name");
            }
            
            // Then pop the value
            StackItem value = stack.pop();
            
            env.defineVariable(name, value, false);
        });       
         
        operations.put("var", (stack, env) -> {
            String name = null;
            
            StackItem nameItem = stack.pop();
            if (nameItem instanceof Primitive) {
                Primitive primitive = (Primitive) nameItem;
                if (primitive.isString()) {
                    name = primitive.getStringValue();
                }
            }
            
            if (name == null) {
                throw new IllegalArgumentException("Expected string for variable name");
            }
            
            Variable variable = env.getVariable(name);
            stack.push(variable);
        });
        
        operations.put("observe", (stack, env) -> {
            String name = null;
            
            StackItem nameItem = stack.pop();
            if (nameItem instanceof Primitive) {
                Primitive primitive = (Primitive) nameItem;
                if (primitive.isString()) {
                    name = primitive.getStringValue();
                }
            }
            
            if (name == null) {
                throw new IllegalArgumentException("Expected string for variable name");
            }
            
            StackItem data = stack.pop();
            Variable variable = env.getVariable(name);
            
            if (!variable.isStochastic()) {
                throw new IllegalArgumentException("Cannot observe deterministic variable");
            }
            
            variable.setObservedData(data);
        });
        
        // Core continuous distributions (PhyloSpec-compliant)
        operations.put("normal", (stack, env) -> {
            // Check against PhyloSpec signature
            DistributionSignature signature = DistributionRegistry.getDistribution("Normal");
            
            Parameter sd = stack.pop(Parameter.class);
            Parameter mean = stack.pop(Parameter.class);
            
            // Validate types (to be implemented)
            
            Normal dist = new Normal(mean, sd);
            stack.push(dist);
        });
        
        operations.put("lognormal", (stack, env) -> {
            // PhyloSpec: LogNormal(meanlog: Real, sdlog: PositiveReal) -> PositiveReal
            Parameter sdlog = stack.pop(Parameter.class);
            Parameter meanlog = stack.pop(Parameter.class);
            
            LogNormal dist = new LogNormal(meanlog, sdlog);
            stack.push(dist);
        });
        
        operations.put("exponential", (stack, env) -> {
            // PhyloSpec: Exponential(rate: PositiveReal) -> PositiveReal
            Parameter rate = stack.pop(Parameter.class);
            
            Exponential dist = new Exponential(rate);
            stack.push(dist);
        });
        
        operations.put("gamma", (stack, env) -> {
            // PhyloSpec: Gamma(shape: PositiveReal, rate: PositiveReal) -> PositiveReal
            Parameter rate = stack.pop(Parameter.class);
            Parameter shape = stack.pop(Parameter.class);
            
            Gamma dist = new Gamma(shape, rate);
            stack.push(dist);
        });
        
        operations.put("beta", (stack, env) -> {
            // PhyloSpec: Beta(alpha: PositiveReal, beta: PositiveReal) -> Probability
            Parameter beta = stack.pop(Parameter.class);
            Parameter alpha = stack.pop(Parameter.class);
            
            // Beta distribution (to be implemented)
            throw new UnsupportedOperationException("Beta distribution not yet implemented");
        });
        
        operations.put("dirichlet", (stack, env) -> {
            // PhyloSpec: Dirichlet(alpha: Vector<PositiveReal>) -> Simplex
            Parameter concentrationParams = stack.pop(Parameter.class);
            
            Dirichlet dist = new Dirichlet(concentrationParams);
            stack.push(dist);
        });
        
        operations.put("uniform", (stack, env) -> {
            // PhyloSpec: Uniform(lower: Real, upper: Real) -> Real
            Parameter upper = stack.pop(Parameter.class);
            Parameter lower = stack.pop(Parameter.class);
            
            // Uniform distribution (to be implemented)
            throw new UnsupportedOperationException("Uniform distribution not yet implemented");
        });
        
        // Tree distributions (PhyloSpec-compliant)
        operations.put("yule", (stack, env) -> {
            // PhyloSpec: Yule(birthRate: PositiveReal) -> Tree
            Parameter birthRate = stack.pop(Parameter.class);
            
            // Create a Yule process with the birth rate
            Yule yule = new Yule(birthRate);
            stack.push(yule);
        });
        
        operations.put("birthdeath", (stack, env) -> {
            // PhyloSpec: BirthDeath(birthRate: PositiveReal, deathRate: PositiveReal, rootHeight: PositiveReal?) -> Tree
            // The rootHeight parameter is optional
            
            // Check if there's a third parameter (rootHeight is optional)
            Parameter deathRate = stack.pop(Parameter.class);
            Parameter birthRate = stack.pop(Parameter.class);
            
            // For now, just use the constructor with required parameters
            BirthDeath dist = new BirthDeath(birthRate, deathRate);
            stack.push(dist);
        });
        
        operations.put("coalescent", (stack, env) -> {
            // PhyloSpec: Coalescent(populationSize: PositiveReal) -> Tree
            Parameter popSize = stack.pop(Parameter.class);
            
            Coalescent dist = new Coalescent(popSize);
            stack.push(dist);
        });
        
        operations.put("fossilbirthdeath", (stack, env) -> {
            // PhyloSpec: FossilBirthDeath(birthRate: PositiveReal, deathRate: PositiveReal, samplingRate: PositiveReal, rho: Probability) -> TimeTree
            Parameter rho = stack.pop(Parameter.class);
            Parameter samplingRate = stack.pop(Parameter.class);
            Parameter deathRate = stack.pop(Parameter.class);
            Parameter birthRate = stack.pop(Parameter.class);
            
            // FossilBirthDeath distribution (to be implemented)
            throw new UnsupportedOperationException("FossilBirthDeath process not yet implemented");
        });
                
        // Nucleotide substitution models (PhyloSpec-compliant)
        operations.put("jc69", (stack, env) -> {
            // PhyloSpec: JC69() -> QMatrix
            // No parameters for JC69
            
            // JC69 model (to be implemented)
            throw new UnsupportedOperationException("JC69 model not yet implemented");
        });
        
        operations.put("k80", (stack, env) -> {
            // PhyloSpec: K80(kappa: PositiveReal) -> QMatrix
            Parameter kappa = stack.pop(Parameter.class);
            
            // K80 model (to be implemented)
            throw new UnsupportedOperationException("K80 model not yet implemented");
        });
        
        operations.put("f81", (stack, env) -> {
            // PhyloSpec: F81(baseFrequencies: Simplex) -> QMatrix
            Parameter baseFreqs = stack.pop(Parameter.class);
            
            // F81 model (to be implemented)
            throw new UnsupportedOperationException("F81 model not yet implemented");
        });
        
        operations.put("hky", (stack, env) -> {
            // PhyloSpec: HKY(kappa: PositiveReal, baseFrequencies: Simplex) -> QMatrix
            Parameter baseFreqs = stack.pop(Parameter.class);
            Parameter kappa = stack.pop(Parameter.class);
            
            HKY model = new HKY(kappa, baseFreqs);
            stack.push(model);
        });
        
        operations.put("gtr", (stack, env) -> {
            // PhyloSpec: GTR(rateMatrix: Vector<PositiveReal>, baseFrequencies: Simplex) -> QMatrix
            Parameter baseFreqs = stack.pop(Parameter.class);
            Parameter rates = stack.pop(Parameter.class);
            
            GTR model = new GTR(rates, baseFreqs);
            stack.push(model);
        });
        
        // Protein models (PhyloSpec-compliant)
        operations.put("wag", (stack, env) -> {
            // PhyloSpec: WAG(freqsModel: Boolean?) -> QMatrix
            Parameter freqsModel = null;
            // Check if there's a parameter
            if (!stack.isEmpty() && stack.peek() instanceof Parameter) {
                freqsModel = stack.pop(Parameter.class);
            }
            
            // WAG model (to be implemented)
            throw new UnsupportedOperationException("WAG model not yet implemented");
        });
        
        operations.put("jtt", (stack, env) -> {
            // PhyloSpec: JTT(freqsModel: Boolean?) -> QMatrix
            Parameter freqsModel = null;
            // Check if there's a parameter
            if (!stack.isEmpty() && stack.peek() instanceof Parameter) {
                freqsModel = stack.pop(Parameter.class);
            }
            
            // JTT model (to be implemented)
            throw new UnsupportedOperationException("JTT model not yet implemented");
        });
        
        operations.put("lg", (stack, env) -> {
            // PhyloSpec: LG(freqsModel: Boolean?) -> QMatrix
            Parameter freqsModel = null;
            // Check if there's a parameter
            if (!stack.isEmpty() && stack.peek() instanceof Parameter) {
                freqsModel = stack.pop(Parameter.class);
            }
            
            // LG model (to be implemented)
            throw new UnsupportedOperationException("LG model not yet implemented");
        });
        
        // Codon models (PhyloSpec-compliant)
        operations.put("gy94", (stack, env) -> {
            // PhyloSpec: GY94(omega: PositiveReal, kappa: PositiveReal, codonFrequencies: Simplex) -> QMatrix
            Parameter codonFreqs = stack.pop(Parameter.class);
            Parameter kappa = stack.pop(Parameter.class);
            Parameter omega = stack.pop(Parameter.class);
            
            // GY94 model (to be implemented)
            throw new UnsupportedOperationException("GY94 model not yet implemented");
        });
        
        // Rate heterogeneity functions (PhyloSpec-compliant)
        operations.put("discretegamma", (stack, env) -> {
            // PhyloSpec: DiscreteGamma(shape: PositiveReal, categories: PosInteger) -> Vector<PositiveReal>
            Parameter categories = stack.pop(Parameter.class);
            Parameter shape = stack.pop(Parameter.class);
            
            // DiscreteGamma model (to be implemented)
            throw new UnsupportedOperationException("DiscreteGamma model not yet implemented");
        });
        
        operations.put("freerates", (stack, env) -> {
            // PhyloSpec: FreeRates(rates: Vector<PositiveReal>, weights: Simplex) -> Vector<PositiveReal>
            Parameter weights = stack.pop(Parameter.class);
            Parameter rates = stack.pop(Parameter.class);
            
            // FreeRates model (to be implemented)
            throw new UnsupportedOperationException("FreeRates model not yet implemented");
        });
        
        operations.put("invariantsites", (stack, env) -> {
            // PhyloSpec: InvariantSites(proportion: Probability) -> Vector<Real>
            Parameter proportion = stack.pop(Parameter.class);
            
            // InvariantSites model (to be implemented)
            throw new UnsupportedOperationException("InvariantSites model not yet implemented");
        });
        
        operations.put("strictclock", (stack, env) -> {
            // PhyloSpec: StrictClock(rate: PositiveReal) -> Vector<PositiveReal>
            Parameter rate = stack.pop(Parameter.class);
            
            // StrictClock model (to be implemented)
            throw new UnsupportedOperationException("StrictClock model not yet implemented");
        });
        
        operations.put("uncorrelatedlognormal", (stack, env) -> {
            // PhyloSpec: UncorrelatedLognormal(mean: Real, stdev: PositiveReal) -> Vector<PositiveReal>
            Parameter stdev = stack.pop(Parameter.class);
            Parameter mean = stack.pop(Parameter.class);
            
            // UncorrelatedLognormal model (to be implemented)
            throw new UnsupportedOperationException("UncorrelatedLognormal model not yet implemented");
        });
        
        operations.put("uncorrelatedexponential", (stack, env) -> {
            // PhyloSpec: UncorrelatedExponential(mean: PositiveReal) -> Vector<PositiveReal>
            Parameter mean = stack.pop(Parameter.class);
            
            // UncorrelatedExponential model (to be implemented)
            throw new UnsupportedOperationException("UncorrelatedExponential model not yet implemented");
        });
        
        // Tree functions (PhyloSpec-compliant)
        operations.put("mrca", (stack, env) -> {
            // PhyloSpec: mrca(tree: Tree, taxa: TaxonSet) -> TreeNode
            Parameter taxa = stack.pop(Parameter.class);
            Parameter tree = stack.pop(Parameter.class);
            
            // MRCA function (to be implemented)
            throw new UnsupportedOperationException("MRCA function not yet implemented");
        });
        
        operations.put("treeheight", (stack, env) -> {
            // PhyloSpec: treeHeight(tree: Tree) -> Real
            Parameter tree = stack.pop(Parameter.class);
            
            // treeHeight function (to be implemented)
            throw new UnsupportedOperationException("treeHeight function not yet implemented");
        });
        
        // Sequence evolution models (PhyloSpec-compliant)
        operations.put("phyloctmc", (stack, env) -> {
            // PhyloSpec: PhyloCTMC<A>(tree: Tree, Q: QMatrix, siteRates: Vector<PositiveReal>?, branchRates: Vector<PositiveReal>?) -> Alignment<A>
            
            // Check how many parameters we have
            // Our current PhyloCTMC constructor only supports tree and substModel (2 params)
            // or tree, substModel, siteRates, branchRates (4 params)
            
            // Check if we have the siteRates parameter on the stack
            StackItem potentialSiteRates = null;
            if (stack.size() >= 3) {
                potentialSiteRates = stack.peek();
            }
            
            // Based on PhyloCTMC constructors, we either need 2 or 4 parameters
            Parameter substModel = stack.pop(Parameter.class);
            Parameter tree = stack.pop(Parameter.class);
            
            // Create PhyloCTMC model with only required parameters
            PhyloCTMC model = new PhyloCTMC(tree, substModel);
            stack.push(model);
        });
                
        operations.put("phylobm", (stack, env) -> {
            // PhyloSpec: PhyloBM(tree: Tree, sigma: PositiveReal, rootValue: Real) -> Vector<Real>
            Parameter rootValue = stack.pop(Parameter.class);
            Parameter sigma = stack.pop(Parameter.class);
            Parameter tree = stack.pop(Parameter.class);
            
            // PhyloBM model (to be implemented)
            throw new UnsupportedOperationException("PhyloBM model not yet implemented");
        });
        
        operations.put("phyloou", (stack, env) -> {
            // PhyloSpec: PhyloOU(tree: Tree, sigma: PositiveReal, alpha: PositiveReal, optimum: Real) -> Vector<Real>
            Parameter optimum = stack.pop(Parameter.class);
            Parameter alpha = stack.pop(Parameter.class);
            Parameter sigma = stack.pop(Parameter.class);
            Parameter tree = stack.pop(Parameter.class);
            
            // PhyloOU model (to be implemented)
            throw new UnsupportedOperationException("PhyloOU model not yet implemented");
        });
        
        // Math functions (PhyloSpec-compliant)
        operations.put("vectorelement", (stack, env) -> {
            // PhyloSpec: vectorElement(vector: Vector<T>, index: Integer) -> T
            Parameter index = stack.pop(Parameter.class);
            Parameter vector = stack.pop(Parameter.class);
            
            // vectorElement function (to be implemented)
            throw new UnsupportedOperationException("vectorElement function not yet implemented");
        });
        
        // Data operations (PhyloSpec-compatible)
        operations.put("sequence", (stack, env) -> {
            String dnaString = null;
            String taxonName = null;
            
            StackItem dnaItem = stack.pop();
            if (dnaItem instanceof Primitive) {
                Primitive primitive = (Primitive) dnaItem;
                if (primitive.isString()) {
                    dnaString = primitive.getStringValue();
                }
            }
            
            StackItem taxonItem = stack.pop();
            if (taxonItem instanceof Primitive) {
                Primitive primitive = (Primitive) taxonItem;
                if (primitive.isString()) {
                    taxonName = primitive.getStringValue();
                }
            }
            
            if (taxonName == null) {
                throw new IllegalArgumentException("Expected string for taxon name");
            }
            
            if (dnaString == null) {
                throw new IllegalArgumentException("Expected string for DNA sequence");
            }
            
            Sequence sequence = new Sequence(taxonName, dnaString);
            stack.push(sequence);
        });
        
        // Constraint functions (PhyloSpec-compliant)
        operations.put("lessthan", (stack, env) -> {
            // PhyloSpec: LessThan(left: Real, right: Real) -> Constraint
            Parameter right = stack.pop(Parameter.class);
            Parameter left = stack.pop(Parameter.class);
            
            // LessThan constraint (to be implemented)
            throw new UnsupportedOperationException("LessThan constraint not yet implemented");
        });
        
        operations.put("greaterthan", (stack, env) -> {
            // PhyloSpec: GreaterThan(left: Real, right: Real) -> Constraint
            Parameter right = stack.pop(Parameter.class);
            Parameter left = stack.pop(Parameter.class);
            
            // GreaterThan constraint (to be implemented)
            throw new UnsupportedOperationException("GreaterThan constraint not yet implemented");
        });
        
        // Add implementations for all other PhyloSpec functions...
    }
    
    /**
     * Returns whether the parser is at the end of the token list.
     * 
     * @return true if at the end, false otherwise
     */
    private boolean isAtEnd() {
        return position >= tokens.size() || tokens.get(position).getType() == TokenType.EOF;
    }
    
    /**
     * Returns the current token without advancing.
     * 
     * @return The current token
     */
    private Token peek() {
        return tokens.get(position);
    }
    
    /**
     * Advances to the next token and returns the previous one.
     * 
     * @return The previous token
     */
    private Token advance() {
        if (!isAtEnd()) {
            position++;
        }
        return tokens.get(position - 1);
    }
    
    /**
     * Validates if a parameter conforms to a PhyloSpec type.
     * 
     * @param param The parameter to validate
     * @param expectedType The expected PhyloSpec type
     * @return true if the parameter is valid for the type, false otherwise
     */
    private boolean validateParameterType(Parameter param, PhyloSpecType expectedType) {
        // Implementation of type checking based on PhyloSpec type system
        // This is a stub - full implementation would check against the PhyloSpec type system
        return true;
    }
}