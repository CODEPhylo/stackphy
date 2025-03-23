package io.github.stackphy.parser;

import io.github.stackphy.model.Parameter;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for tree operations.
 */
public class TreeOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("mrca", this::mrcaOperation);
        operations.put("treeheight", this::treeHeightOperation);
        operations.put("nodeage", this::nodeAgeOperation);
        operations.put("branchlength", this::branchLengthOperation);
        operations.put("distancematrix", this::distanceMatrixOperation);
        operations.put("descendanttaxa", this::descendantTaxaOperation);
    }
    
    /**
     * MRCA operation implementation
     */
    private void mrcaOperation(Stack stack, Environment env) {
        // PhyloSpec: mrca(tree: Tree, taxa: TaxonSet) -> TreeNode
        Parameter taxa = stack.pop(Parameter.class);
        Parameter tree = stack.pop(Parameter.class);
        
        // MRCA function (to be implemented)
        throw new UnsupportedOperationException("MRCA function not yet implemented");
    }
    
    /**
     * TreeHeight operation implementation
     */
    private void treeHeightOperation(Stack stack, Environment env) {
        // PhyloSpec: treeHeight(tree: Tree) -> Real
        Parameter tree = stack.pop(Parameter.class);
        
        // treeHeight function (to be implemented)
        throw new UnsupportedOperationException("treeHeight function not yet implemented");
    }
    
    /**
     * NodeAge operation implementation
     */
    private void nodeAgeOperation(Stack stack, Environment env) {
        // PhyloSpec: nodeAge(node: TreeNode) -> Real
        Parameter node = stack.pop(Parameter.class);
        
        // nodeAge function (to be implemented)
        throw new UnsupportedOperationException("nodeAge function not yet implemented");
    }
    
    /**
     * BranchLength operation implementation
     */
    private void branchLengthOperation(Stack stack, Environment env) {
        // PhyloSpec: branchLength(node: TreeNode) -> Real
        Parameter node = stack.pop(Parameter.class);
        
        // branchLength function (to be implemented)
        throw new UnsupportedOperationException("branchLength function not yet implemented");
    }
    
    /**
     * DistanceMatrix operation implementation
     */
    private void distanceMatrixOperation(Stack stack, Environment env) {
        // PhyloSpec: distanceMatrix(tree: Tree) -> Matrix<Real>
        Parameter tree = stack.pop(Parameter.class);
        
        // distanceMatrix function (to be implemented)
        throw new UnsupportedOperationException("distanceMatrix function not yet implemented");
    }
    
    /**
     * DescendantTaxa operation implementation
     */
    private void descendantTaxaOperation(Stack stack, Environment env) {
        // PhyloSpec: descendantTaxa(node: TreeNode) -> TaxonSet
        Parameter node = stack.pop(Parameter.class);
        
        // descendantTaxa function (to be implemented)
        throw new UnsupportedOperationException("descendantTaxa function not yet implemented");
    }
}