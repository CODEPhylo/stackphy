package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.Sequence;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for sequence operations.
 */
public class SequenceOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("sequence", this::sequenceOperation);
        operations.put("alignment", this::alignmentOperation);
    }
    
    /**
     * Sequence operation implementation
     */
    private void sequenceOperation(Stack stack, Environment env) {
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
    }
    
    /**
     * Alignment operation implementation
     */
    private void alignmentOperation(Stack stack, Environment env) {
        // PhyloSpec: Alignment(sequences: Vector<Sequence>) -> Alignment
        // This is a placeholder for a future implementation
        throw new UnsupportedOperationException("Alignment operation not yet implemented");
    }
}