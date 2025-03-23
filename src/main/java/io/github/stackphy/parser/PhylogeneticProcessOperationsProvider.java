package io.github.stackphy.parser;

import io.github.stackphy.distribution.PhyloCTMC;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.model.Variable;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for phylogenetic process operations.
 */
public class PhylogeneticProcessOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("phyloctmc", this::phyloCTMCOperation);
        operations.put("phylobm", this::phyloBMOperation);
        operations.put("phyloou", this::phyloOUOperation);
    }
    
    /**
     * PhyloCTMC operation implementation
     */
    private void phyloCTMCOperation(Stack stack, Environment env) {
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
    }
    
    /**
     * PhyloBM operation implementation
     */
    private void phyloBMOperation(Stack stack, Environment env) {
        // PhyloSpec: PhyloBM(tree: Tree, sigma: PositiveReal, rootValue: Real) -> Vector<Real>
        Parameter rootValue = stack.pop(Parameter.class);
        Parameter sigma = stack.pop(Parameter.class);
        Parameter tree = stack.pop(Parameter.class);
        
        // PhyloBM model (to be implemented)
        throw new UnsupportedOperationException("PhyloBM model not yet implemented");
    }
    
    /**
     * PhyloOU operation implementation
     */
    private void phyloOUOperation(Stack stack, Environment env) {
        // PhyloSpec: PhyloOU(tree: Tree, sigma: PositiveReal, alpha: PositiveReal, optimum: Real) -> Vector<Real>
        Parameter optimum = stack.pop(Parameter.class);
        Parameter alpha = stack.pop(Parameter.class);
        Parameter sigma = stack.pop(Parameter.class);
        Parameter tree = stack.pop(Parameter.class);
        
        // PhyloOU model (to be implemented)
        throw new UnsupportedOperationException("PhyloOU model not yet implemented");
    }
}