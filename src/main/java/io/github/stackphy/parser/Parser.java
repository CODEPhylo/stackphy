package io.github.stackphy.parser;

import io.github.stackphy.distribution.*;
import io.github.stackphy.functions.*;
import io.github.stackphy.model.*;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;
import io.github.stackphy.types.*;
import io.github.stackphy.substitution.*;
import io.github.stackphy.constraints.*;

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
    private boolean inStackCommentMode;
    private StringBuilder stackCommentBuilder;
    
    /**
     * Creates a new parser for the given tokens.
     * 
     * @param tokens The tokens to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.operations = new HashMap<>();
        this.inStackCommentMode = false;
        this.stackCommentBuilder = new StringBuilder();
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
                
                case FUNCTION_START:
                    // Start of a function definition
                    operation = new FunctionStartOperation(token.getLine(), token.getColumn());
                    break;
                    
                case FUNCTION_END:
                    // End of a function definition
                    operation = new FunctionEndOperation(token.getLine(), token.getColumn());
                    break;
                    
                case IDENTIFIER:
                    // User identifier - could be a variable name or a function call
                    String name = token.getValue();
                    
                    // Check if it's a function definition (following a FUNCTION_START)
                    if (!operations.isEmpty() && operations.get(operations.size() - 1) instanceof FunctionStartOperation) {
                        operation = new FunctionNameOperation(name, token.getLine(), token.getColumn());
                    } else {
                        // Check if it's a user-defined function call
                        // This will be resolved at runtime
                        operation = new FunctionCallUserOperation(name, token.getLine(), token.getColumn());
                    	// but this should check if the function exists and fail otherwise!
                    	// throw new StackPhyException("Unexpected identifier: " + token.getValue(), token.getLine(), token.getColumn());

                    }   
                    break;
                
                case PAREN_OPEN:
                    // Start stack comment mode
                    inStackCommentMode = true;
                    stackCommentBuilder = new StringBuilder();
                    // No operation created for opening parenthesis - it's part of the comment
                    break;
                
                case PAREN_CLOSE:
                    if (inStackCommentMode) {
                        // End of stack comment
                        inStackCommentMode = false;
                        operation = new StackCommentOperation(stackCommentBuilder.toString().trim(), 
                                                             token.getLine(), token.getColumn());
                    } else {
                        // Regular closing parenthesis operation (if that's valid in your language)
                        operation = new ValueOperation(")", token.getLine(), token.getColumn());
                    }
                    break;
                    
                // Add a special case for dash tokens in the switch statement
                case DASH:
                    // If we're in stack comment parsing mode, include it as part of the comment
                    // Otherwise, treat it as an operation
                    if (inStackCommentMode) {
                        // Add to stack comment string
                        stackCommentBuilder.append("-");
                    } else {
                        // Handle as operation
                        operation = new NamedOperation("dash", this.operations.get("dash"), token.getLine(), token.getColumn());
                    }
                    break;      
                
                case BRACKET_CLOSE:
                case TILDE:
                case MULTIPLY:              
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
                case DISCRETE_GAMMA_VECTOR:
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
                    String operationName = token.getValue().toLowerCase(); // PhyloSpec operations are case-insensitive
                    if (this.operations.containsKey(operationName)) {
                        operation = new NamedOperation(operationName, this.operations.get(operationName), token.getLine(), token.getColumn());
                    } else {
                        throw new StackPhyException("Unknown operation: " + operationName, token.getLine(), token.getColumn());
                    }
                    break;
                                
                case ERROR:
                    throw new StackPhyException("Syntax error: " + token.getValue(), token.getLine(), token.getColumn());
                
                case EOF:
                    // End of file, stop parsing
                    return operations;
                
                default:
                    // Check if it's a known operation
                    String opName = token.getValue().toLowerCase();
                    if (this.operations.containsKey(opName)) {
                        operation = new NamedOperation(opName, this.operations.get(opName), token.getLine(), token.getColumn());
                    } else {
                        throw new StackPhyException("Unknown operation: " + token.getValue(), token.getLine(), token.getColumn());
                    }
                    break;
            }
            
            if (operation != null) {
                operations.add(operation);
            }
        }
        
        return operations;
    }
    
    // Handle stack comments after FUNCTION_START and function name
    private Operation parseStackComment() throws StackPhyException {
        // Check for opening parenthesis
        if (peek().getType() != TokenType.PAREN_OPEN) {
            return null; // No stack comment
        }
        
        // Skip the opening parenthesis
        advance();
        
        // Collect everything until the closing parenthesis
        StringBuilder comment = new StringBuilder();
        int line = peek().getLine();
        int column = peek().getColumn();
        
        while (!isAtEnd() && peek().getType() != TokenType.PAREN_CLOSE) {
            comment.append(advance().getValue()).append(" ");
        }
        
        // Expect the closing parenthesis
        if (isAtEnd() || peek().getType() != TokenType.PAREN_CLOSE) {
            throw new StackPhyException("Unterminated stack comment", line, column);
        }
        
        // Skip the closing parenthesis
        advance();
        
        return new StackCommentOperation(comment.toString().trim(), line, column);
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
        
        operations.put("*", (stack, env) -> {
            // Pop two values from the stack
            StackItem b = stack.pop();
            StackItem a = stack.pop();
            
            // Both should be numeric values
            if (a instanceof Primitive && b instanceof Primitive) {
                Primitive pa = (Primitive) a;
                Primitive pb = (Primitive) b;
                
                if (pa.isNumeric() && pb.isNumeric()) {
                    double result = pa.getDoubleValue() * pb.getDoubleValue();
                    stack.push(new Primitive(result));
                    return;
                }
            }
            
            throw new IllegalArgumentException("Multiplication requires two numeric values");
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
            Parameter baseFreqs = stack.pop(Parameter.class);
            Parameter kappa = stack.pop(Parameter.class);
            
            // Create the HKY model
            HKY model = new HKY(kappa, baseFreqs);
                        
            // Push the Q matrix, not the model itself
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
        
        // Replace the existing discretegamma operation with this:
        operations.put("discretegamma", (stack, env) -> {
            // PhyloSpec: DiscreteGamma(shape: PositiveReal, categories: PosInteger) -> Vector<PositiveReal>
            Parameter categories = stack.pop(Parameter.class);
            Parameter shape = stack.pop(Parameter.class);
            
            // Create DiscreteGamma distribution
            DiscreteGamma dist = new DiscreteGamma(shape, categories);
            stack.push(dist);
        });
        
        // Add a new operation for DiscreteGammaVector
        operations.put("discretegammavector", (stack, env) -> {
            // PhyloSpec: DiscreteGammaVector(shape: PositiveReal, categories: PosInteger, dimension: PosInteger) -> Vector<PositiveReal>
            Parameter dimension = stack.pop(Parameter.class);
            Parameter categories = stack.pop(Parameter.class);
            Parameter shape = stack.pop(Parameter.class);
            
            // Create DiscreteGammaVector distribution with dimension
            DiscreteGamma dist = new DiscreteGamma(shape, categories, dimension);
            stack.push(dist);
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
        
        operations.put("phyloctmc", (stack, env) -> {
            // PhyloSpec: PhyloCTMC<A>(tree: Tree, Q: QMatrix, siteRates: Vector<PositiveReal>?, branchRates: Vector<PositiveReal>?)
            
            // First check how many parameters we have
            int paramCount = 0;
            if (stack.size() >= 3) paramCount = 3;
            
            // Pop parameters based on what's available
            Parameter siteRates = null;
            
            // If we have a third parameter (siteRates), pop it
            if (paramCount >= 3) {
                siteRates = stack.pop(Parameter.class);
                
                // If it's a variable, resolve it
                if (siteRates instanceof Variable) {
                    Variable siteRatesVar = (Variable) siteRates;
                    siteRates = (Parameter) siteRatesVar.getValue();
                }
            }
            
            // Get substitution model parameter
            Parameter substModel = stack.pop(Parameter.class);
            
            // If it's a variable, resolve it to get the actual model
            if (substModel instanceof Variable) {
                Variable substModelVar = (Variable) substModel;
                substModel = (Parameter) substModelVar.getValue();
            }
            
            // Get tree parameter
            Parameter tree = stack.pop(Parameter.class);
            
            // If it's a variable, resolve it
            if (tree instanceof Variable) {
                Variable treeVar = (Variable) tree;
                tree = (Parameter) treeVar.getValue();
            }
            
            // Now create the PhyloCTMC model
            PhyloCTMC model;
            
            if (siteRates != null) {
                // Call the 4-parameter constructor with null clockRate
                model = new PhyloCTMC(tree, substModel, siteRates, null);
            } else {
                // Call the 2-parameter constructor
                model = new PhyloCTMC(tree, substModel);
            }
            
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
        
        operations.put("lessthan", (stack, env) -> {
            // PhyloSpec: LessThan(left: Real, right: Real) -> Constraint
            Parameter right = stack.pop(Parameter.class);
            Parameter left = stack.pop(Parameter.class);
            
            // Create LessThan constraint
            LessThan constraint = new LessThan(left, right);
            stack.push(constraint);
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