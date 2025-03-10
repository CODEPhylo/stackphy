package io.github.stackphy;

import io.github.stackphy.model.*;
import io.github.stackphy.parser.*;
import io.github.stackphy.runtime.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Main class for the StackPhy application.
 */
public class Main {
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String filePath = args[0];
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            System.exit(1);
        }
        
        if (!file.isFile()) {
            System.err.println("Not a file: " + filePath);
            System.exit(1);
        }
        
        try {
            StackPhyParser parser = new StackPhyParser();
            Environment environment = parser.parseFile(file);
            
            // Print the variables in the environment
            System.out.println("Model parsed successfully.");
            
            // Print stochastic variables
            System.out.println("\nStochastic Variables:");
            Map<String, Variable> stochasticVars = environment.getStochasticVariables();
            
            if (stochasticVars.isEmpty()) {
                System.out.println("  (none)");
            } else {
                for (Map.Entry<String, Variable> entry : stochasticVars.entrySet()) {
                    String name = entry.getKey();
                    Variable variable = entry.getValue();
                    StackItem value = variable.getUnderlyingValue();                    
                    
                    System.out.printf("  %s ~ %s%n", name, getVariableDescription(value));
                    
                    // Print observed data if any
                    if (variable.hasObservedData()) {
                        System.out.printf("    (observed)%n");
                    }
                }
            }
            
            // Print deterministic variables
            System.out.println("\nDeterministic Variables:");
            Map<String, Variable> deterministicVars = environment.getDeterministicVariables();
            
            if (deterministicVars.isEmpty()) {
                System.out.println("  (none)");
            } else {
                for (Map.Entry<String, Variable> entry : deterministicVars.entrySet()) {
                    String name = entry.getKey();
                    Variable variable = entry.getValue();
                    StackItem value = variable.getUnderlyingValue();
                    
                    System.out.printf("  %s = %s%n", name, getVariableDescription(value));
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (StackPhyException e) {
            System.err.println("Error parsing model: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("Usage: stackphy <file.sp>");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  <file.sp>    StackPhy model file to parse");
    }
    
    /**
     * Gets a description of a variable value for printing.
     * 
     * @param value The variable value
     * @return A string description
     */
    private static String getVariableDescription(StackItem value) {
        if (value instanceof Distribution) {
            Distribution dist = (Distribution) value;
            return dist.getDistributionType();
        } else if (value instanceof Model) {
            Model model = (Model) value;
            return model.getModelType();
        } else if (value instanceof Sequence) {
            Sequence seq = (Sequence) value;
            return "Sequence(" + seq.getTaxon() + ")";
        } else if (value instanceof Parameter) {
            Parameter param = (Parameter) value;
            return param.isAnonymous() ? String.valueOf(param.getValue()) : param.getName();
        } else {
            return value.toString();
        }
    }
}
