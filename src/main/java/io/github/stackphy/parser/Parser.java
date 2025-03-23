package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic parser for StackPhy language, updated to use the OperationRegistry.
 */
public class Parser {
    private final List<Token> tokens;
    private int position;
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
        this.inStackCommentMode = false;
        this.stackCommentBuilder = new StringBuilder();
    }
    
    /**
     * Parses the tokens into a list of operations.
     * 
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    public List<Operation> parse() throws StackPhyException {
        List<Operation> operations = new ArrayList<>();
        OperationRegistry registry = OperationRegistry.getInstance();
        
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
                        try {
                            operation = registry.createNamedOperation("dash", token.getLine(), token.getColumn());
                        } catch (StackPhyException e) {
                            throw new StackPhyException("Unknown operation: dash", token.getLine(), token.getColumn());
                        }
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
                    // Named operation from the registry
                    String operationName = token.getValue().toLowerCase(); // PhyloSpec operations are case-insensitive
                    try {
                        operation = registry.createNamedOperation(operationName, token.getLine(), token.getColumn());
                    } catch (StackPhyException e) {
                        throw new StackPhyException("Unknown operation: " + operationName, token.getLine(), token.getColumn());
                    }
                    break;
                                
                case ERROR:
                    throw new StackPhyException("Syntax error: " + token.getValue(), token.getLine(), token.getColumn());
                
                case EOF:
                    // End of file, stop parsing
                    return operations;
                
                default:
                    // Check if it's a known operation in the registry
                    String opName = token.getValue().toLowerCase();
                    try {
                        operation = registry.createNamedOperation(opName, token.getLine(), token.getColumn());
                    } catch (StackPhyException e) {
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
}