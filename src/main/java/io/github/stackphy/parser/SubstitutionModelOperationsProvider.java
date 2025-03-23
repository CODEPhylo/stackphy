package io.github.stackphy.parser;

import io.github.stackphy.model.Parameter;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;
import io.github.stackphy.substitution.*;

import java.util.Map;

/**
 * Provider for substitution model operations.
 */
public class SubstitutionModelOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        // Nucleotide substitution models
        operations.put("jc69", this::jc69Model);
        operations.put("k80", this::k80Model);
        operations.put("f81", this::f81Model);
        operations.put("hky", this::hkyModel);
        operations.put("gtr", this::gtrModel);
        
        // Protein models
        operations.put("wag", this::wagModel);
        operations.put("jtt", this::jttModel);
        operations.put("lg", this::lgModel);
        
        // Codon models
        operations.put("gy94", this::gy94Model);
    }
    
    /**
     * JC69 model implementation
     */
    private void jc69Model(Stack stack, Environment env) {
        // PhyloSpec: JC69() -> QMatrix
        // No parameters for JC69
        
        // JC69 model (to be implemented)
        throw new UnsupportedOperationException("JC69 model not yet implemented");
    }
    
    /**
     * K80 model implementation
     */
    private void k80Model(Stack stack, Environment env) {
        // PhyloSpec: K80(kappa: PositiveReal) -> QMatrix
        Parameter kappa = stack.pop(Parameter.class);
        
        // K80 model (to be implemented)
        throw new UnsupportedOperationException("K80 model not yet implemented");
    }
    
    /**
     * F81 model implementation
     */
    private void f81Model(Stack stack, Environment env) {
        // PhyloSpec: F81(baseFrequencies: Simplex) -> QMatrix
        Parameter baseFreqs = stack.pop(Parameter.class);
        
        // F81 model (to be implemented)
        throw new UnsupportedOperationException("F81 model not yet implemented");
    }
    
    /**
     * HKY model implementation
     */
    private void hkyModel(Stack stack, Environment env) {
        Parameter baseFreqs = stack.pop(Parameter.class);
        Parameter kappa = stack.pop(Parameter.class);
        
        // Create the HKY model
        HKY model = new HKY(kappa, baseFreqs);
                    
        // Push the Q matrix, not the model itself
        stack.push(model);
    }
    
    /**
     * GTR model implementation
     */
    private void gtrModel(Stack stack, Environment env) {
        // PhyloSpec: GTR(rateMatrix: Vector<PositiveReal>, baseFrequencies: Simplex) -> QMatrix
        Parameter baseFreqs = stack.pop(Parameter.class);
        Parameter rates = stack.pop(Parameter.class);
        
        GTR model = new GTR(rates, baseFreqs);
        stack.push(model);
    }
    
    /**
     * WAG model implementation
     */
    private void wagModel(Stack stack, Environment env) {
        // PhyloSpec: WAG(freqsModel: Boolean?) -> QMatrix
        Parameter freqsModel = null;
        // Check if there's a parameter
        if (!stack.isEmpty() && stack.peek() instanceof Parameter) {
            freqsModel = stack.pop(Parameter.class);
        }
        
        // WAG model (to be implemented)
        throw new UnsupportedOperationException("WAG model not yet implemented");
    }
    
    /**
     * JTT model implementation
     */
    private void jttModel(Stack stack, Environment env) {
        // PhyloSpec: JTT(freqsModel: Boolean?) -> QMatrix
        Parameter freqsModel = null;
        // Check if there's a parameter
        if (!stack.isEmpty() && stack.peek() instanceof Parameter) {
            freqsModel = stack.pop(Parameter.class);
        }
        
        // JTT model (to be implemented)
        throw new UnsupportedOperationException("JTT model not yet implemented");
    }
    
    /**
     * LG model implementation
     */
    private void lgModel(Stack stack, Environment env) {
        // PhyloSpec: LG(freqsModel: Boolean?) -> QMatrix
        Parameter freqsModel = null;
        // Check if there's a parameter
        if (!stack.isEmpty() && stack.peek() instanceof Parameter) {
            freqsModel = stack.pop(Parameter.class);
        }
        
        // LG model (to be implemented)
        throw new UnsupportedOperationException("LG model not yet implemented");
    }
    
    /**
     * GY94 model implementation
     */
    private void gy94Model(Stack stack, Environment env) {
        // PhyloSpec: GY94(omega: PositiveReal, kappa: PositiveReal, codonFrequencies: Simplex) -> QMatrix
        Parameter codonFreqs = stack.pop(Parameter.class);
        Parameter kappa = stack.pop(Parameter.class);
        Parameter omega = stack.pop(Parameter.class);
        
        // GY94 model (to be implemented)
        throw new UnsupportedOperationException("GY94 model not yet implemented");
    }
}