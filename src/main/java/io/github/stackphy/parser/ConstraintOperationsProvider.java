package io.github.stackphy.parser;

import io.github.stackphy.constraints.LessThan;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for constraint operations.
 */
public class ConstraintOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("lessthan", this::lessThanOperation);
        operations.put("greaterthan", this::greaterThanOperation);
        operations.put("equals", this::equalsOperation);
        operations.put("bounded", this::boundedOperation);
        operations.put("sumto", this::sumToOperation);
        operations.put("monophyly", this::monophylyOperation);
        operations.put("calibration", this::calibrationOperation);
    }
    
    /**
     * LessThan constraint operation implementation
     */
    private void lessThanOperation(Stack stack, Environment env) {
        // PhyloSpec: LessThan(left: Real, right: Real) -> Constraint
        Parameter right = stack.pop(Parameter.class);
        Parameter left = stack.pop(Parameter.class);
        
        // Create LessThan constraint
        LessThan constraint = new LessThan(left, right);
        stack.push(constraint);
    }
    
    /**
     * GreaterThan constraint operation implementation
     */
    private void greaterThanOperation(Stack stack, Environment env) {
        // PhyloSpec: GreaterThan(left: Real, right: Real) -> Constraint
        Parameter right = stack.pop(Parameter.class);
        Parameter left = stack.pop(Parameter.class);
        
        // GreaterThan constraint (to be implemented)
        throw new UnsupportedOperationException("GreaterThan constraint not yet implemented");
    }
    
    /**
     * Equals constraint operation implementation
     */
    private void equalsOperation(Stack stack, Environment env) {
        // PhyloSpec: Equals(left: Real, right: Real) -> Constraint
        Parameter right = stack.pop(Parameter.class);
        Parameter left = stack.pop(Parameter.class);
        
        // Equals constraint (to be implemented)
        throw new UnsupportedOperationException("Equals constraint not yet implemented");
    }
    
    /**
     * Bounded constraint operation implementation
     */
    private void boundedOperation(Stack stack, Environment env) {
        // PhyloSpec: Bounded(value: Real, lower: Real, upper: Real) -> Constraint
        Parameter upper = stack.pop(Parameter.class);
        Parameter lower = stack.pop(Parameter.class);
        Parameter value = stack.pop(Parameter.class);
        
        // Bounded constraint (to be implemented)
        throw new UnsupportedOperationException("Bounded constraint not yet implemented");
    }
    
    /**
     * SumTo constraint operation implementation
     */
    private void sumToOperation(Stack stack, Environment env) {
        // PhyloSpec: SumTo(values: Vector<Real>, target: Real) -> Constraint
        Parameter target = stack.pop(Parameter.class);
        Parameter values = stack.pop(Parameter.class);
        
        // SumTo constraint (to be implemented)
        throw new UnsupportedOperationException("SumTo constraint not yet implemented");
    }
    
    /**
     * Monophyly constraint operation implementation
     */
    private void monophylyOperation(Stack stack, Environment env) {
        // PhyloSpec: Monophyly(tree: Tree, taxa: TaxonSet) -> Constraint
        Parameter taxa = stack.pop(Parameter.class);
        Parameter tree = stack.pop(Parameter.class);
        
        // Monophyly constraint (to be implemented)
        throw new UnsupportedOperationException("Monophyly constraint not yet implemented");
    }
    
    /**
     * Calibration constraint operation implementation
     */
    private void calibrationOperation(Stack stack, Environment env) {
        // PhyloSpec: Calibration(node: TreeNode, distribution: Distribution<Real>) -> Constraint
        Parameter distribution = stack.pop(Parameter.class);
        Parameter node = stack.pop(Parameter.class);
        
        // Calibration constraint (to be implemented)
        throw new UnsupportedOperationException("Calibration constraint not yet implemented");
    }
}