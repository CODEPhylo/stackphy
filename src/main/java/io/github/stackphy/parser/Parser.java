package io.github.stackphy.parser;

import io.github.stackphy.distribution.*;
import io.github.stackphy.model.*;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;
import io.github.stackphy.substitution.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the StackPhy language.
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
                case YULE:
                case BIRTH_DEATH:
                case COALESCENT:
                case HKY:
                case GTR:
                case PHYLO_CTMC:
                case SEQUENCE:
                    // Named operation
                    String name = token.getValue();
                    if (this.operations.containsKey(name)) {
                        operation = new NamedOperation(name, this.operations.get(name), token.getLine(), token.getColumn());
                    } else {
                        throw new StackPhyException("Unknown operation: " + name, token.getLine(), token.getColumn());
                    }
                    break;
                
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
     * Registers all operations.
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
        
        // Distribution operations
        operations.put("normal", (stack, env) -> {
            Parameter sd = stack.pop(Parameter.class);
            Parameter mean = stack.pop(Parameter.class);
            
            Normal dist = new Normal(mean, sd);
            stack.push(dist);
        });
        
        operations.put("lognormal", (stack, env) -> {
            Parameter sd = stack.pop(Parameter.class);
            Parameter mean = stack.pop(Parameter.class);
            
            LogNormal dist = new LogNormal(mean, sd);
            stack.push(dist);
        });
        
        operations.put("exponential", (stack, env) -> {
            Parameter rate = stack.pop(Parameter.class);
            
            Exponential dist = new Exponential(rate);
            stack.push(dist);
        });
        
        operations.put("dirichlet", (stack, env) -> {
            Parameter concentrationParams = stack.pop(Parameter.class);
            
            Dirichlet dist = new Dirichlet(concentrationParams);
            stack.push(dist);
        });
        
        operations.put("gamma", (stack, env) -> {
            Parameter rate = stack.pop(Parameter.class);
            Parameter shape = stack.pop(Parameter.class);
            
            // Create gamma distribution (to be implemented)
            // Gamma dist = new Gamma(shape, rate);
            // stack.push(dist);
            throw new UnsupportedOperationException("Gamma distribution not yet implemented");
        });
        
        operations.put("yule", (stack, env) -> {
            Parameter birthRate = stack.pop(Parameter.class);
            
            // Create a Yule process with the birth rate
            Yule yule = new Yule(birthRate);
            stack.push(yule);
        });
        
        operations.put("birthDeath", (stack, env) -> {
            Parameter deathRate = stack.pop(Parameter.class);
            Parameter birthRate = stack.pop(Parameter.class);
            
            // Create birth-death process (to be implemented)
            // BirthDeath dist = new BirthDeath(birthRate, deathRate);
            // stack.push(dist);
            throw new UnsupportedOperationException("Birth-death process not yet implemented");
        });
        
        operations.put("coalescent", (stack, env) -> {
            Parameter popSize = stack.pop(Parameter.class);
            
            // Create coalescent process (to be implemented)
            // Coalescent dist = new Coalescent(popSize);
            // stack.push(dist);
            throw new UnsupportedOperationException("Coalescent process not yet implemented");
        });
        
        // Model operations
        operations.put("hky", (stack, env) -> {
            Parameter baseFreqs = stack.pop(Parameter.class);
            Parameter kappa = stack.pop(Parameter.class);
            
            HKY model = new HKY(kappa, baseFreqs);
            stack.push(model);
        });
        
        operations.put("gtr", (stack, env) -> {
            Parameter baseFreqs = stack.pop(Parameter.class);
            Parameter rates = stack.pop(Parameter.class);
            
            GTR model = new GTR(rates, baseFreqs);
            stack.push(model);
        });
        
        operations.put("phyloCTMC", (stack, env) -> {
            Parameter substModel = stack.pop(Parameter.class);
            Parameter tree = stack.pop(Parameter.class);
            
            // Create a PhyloCTMC model with the tree and substitution model
            PhyloCTMC model = new PhyloCTMC(tree, substModel);
            stack.push(model);
        });      
          
        // Data operations
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
