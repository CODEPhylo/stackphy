package io.github.stackphy.parser;

import io.github.stackphy.functions.DistributionRegistry;
import io.github.stackphy.functions.DistributionSignature;
import io.github.stackphy.functions.FunctionRegistry;
import io.github.stackphy.functions.FunctionSignature;
import io.github.stackphy.model.Distribution;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.ArrayList;
import java.util.List;

/**
 * Operation that represents a function or distribution call.
 */
public class FunctionCallOperation implements Operation {
    private final String name;
    private final List<Operation> argumentOps;
    private final int line;
    private final int column;
    
    /**
     * Creates a new function call operation.
     * 
     * @param name The function or distribution name
     * @param argumentOps The argument operations
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public FunctionCallOperation(String name, List<Operation> argumentOps, int line, int column) {
        this.name = name;
        this.argumentOps = argumentOps;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        try {
            // Check if it's a function
            FunctionSignature functionSignature = FunctionRegistry.getFunction(name);
            
            if (functionSignature != null) {
                executeFunction(functionSignature, stack, env);
                return;
            }
            
            // Check if it's a distribution
            DistributionSignature distributionSignature = DistributionRegistry.getDistribution(name);
            
            if (distributionSignature != null) {
                executeDistribution(distributionSignature, stack, env);
                return;
            }
            
            // Unknown function or distribution
            throw new StackPhyException("Unknown function or distribution: " + name, line, column);
        } catch (Exception e) {
            throw new StackPhyException("Error executing function call '" + name + "': " + e.getMessage(), e, line, column);
        }
    }
    
    /**
     * Executes a function.
     * 
     * @param signature The function signature
     * @param stack The stack
     * @param env The environment
     * @throws Exception if an error occurs
     */
    private void executeFunction(FunctionSignature signature, Stack stack, Environment env) throws Exception {
        // Execute arguments in reverse order so they can be popped in the correct order
        for (int i = argumentOps.size() - 1; i >= 0; i--) {
            argumentOps.get(i).execute(stack, env);
        }
        
        // Now the arguments are on the stack, handle based on function name
        switch (name) {
            case "JC69":
                // Create JC69 substitution model
                // Implementation depends on specific classes
                break;
                
            case "K80":
                // Create K80 model with kappa parameter
                Parameter kappa = stack.pop(Parameter.class);
                // Implementation depends on specific classes
                break;
                
            case "HKY":
                // Create HKY model with two parameters
                Parameter baseFreqs = stack.pop(Parameter.class);
                Parameter kappaHKY = stack.pop(Parameter.class);
                // Implementation depends on specific classes
                break;
                
            case "GTR":
                // Create GTR model
                Parameter gtrBaseFreqs = stack.pop(Parameter.class);
                Parameter rateMatrix = stack.pop(Parameter.class);
                // Implementation depends on specific classes
                break;
                
            case "DiscreteGamma":
                // Create discrete gamma rates
                Parameter categories = stack.pop(Parameter.class);
                Parameter shape = stack.pop(Parameter.class);
                // Implementation depends on specific classes
                break;
                
            // Add cases for all other PhyloSpec functions
            // Tree functions, math functions, etc.
                
            default:
                throw new IllegalArgumentException("Unimplemented function: " + name);
        }
    }
    
    /**
     * Executes a distribution.
     * 
     * @param signature The distribution signature
     * @param stack The stack
     * @param env The environment
     * @throws Exception if an error occurs
     */
    private void executeDistribution(DistributionSignature signature, Stack stack, Environment env) throws Exception {
        // Execute arguments in reverse order so they can be popped in the correct order
        for (int i = argumentOps.size() - 1; i >= 0; i--) {
            argumentOps.get(i).execute(stack, env);
        }
        
        // Now the arguments are on the stack, handle based on distribution name
        switch (name) {
            case "Normal":
                // Create normal distribution
                Parameter sd = stack.pop(Parameter.class);
                Parameter mean = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.Normal(mean, sd));
                break;
                
            case "LogNormal":
                // Create log-normal distribution
                Parameter sdlog = stack.pop(Parameter.class);
                Parameter meanlog = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.LogNormal(meanlog, sdlog));
                break;
                
            case "Exponential":
                // Create exponential distribution
                Parameter rate = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.Exponential(rate));
                break;
                
            case "Gamma":
                // Create gamma distribution
                Parameter gammaRate = stack.pop(Parameter.class);
                Parameter shape = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.Gamma(shape, gammaRate));
                break;
                
            case "Dirichlet":
                // Create Dirichlet distribution
                Parameter concentrationParams = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.Dirichlet(concentrationParams));
                break;
                
            case "Yule":
                // Create Yule process
                Parameter birthRate = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.Yule(birthRate));
                break;
                
            case "BirthDeath":
                // Create birth-death process
                Parameter deathRate = stack.pop(Parameter.class);
                Parameter bdBirthRate = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.BirthDeath(bdBirthRate, deathRate));
                break;
                
            case "Coalescent":
                // Create coalescent process
                Parameter popSize = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.Coalescent(popSize));
                break;
                
            case "PhyloCTMC":
                // Create phylogenetic CTMC
                Parameter substModel = stack.pop(Parameter.class);
                Parameter tree = stack.pop(Parameter.class);
                stack.push(new io.github.stackphy.distribution.PhyloCTMC(tree, substModel));
                break;
                
            // Add cases for all other PhyloSpec distributions
                
            default:
                throw new IllegalArgumentException("Unimplemented distribution: " + name);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name);
        builder.append('(');
        
        for (int i = 0; i < argumentOps.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(argumentOps.get(i));
        }
        
        builder.append(')');
        return builder.toString();
    }
}