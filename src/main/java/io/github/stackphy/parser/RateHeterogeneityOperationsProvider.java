package io.github.stackphy.parser;

import io.github.stackphy.distribution.DiscreteGamma;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for rate heterogeneity operations.
 */
public class RateHeterogeneityOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("discretegamma", this::discreteGammaOperation);
        operations.put("discretegammavector", this::discreteGammaVectorOperation);
        operations.put("freerates", this::freeRatesOperation);
        operations.put("invariantsites", this::invariantSitesOperation);
        operations.put("strictclock", this::strictClockOperation);
        operations.put("uncorrelatedlognormal", this::uncorrelatedLognormalOperation);
        operations.put("uncorrelatedexponential", this::uncorrelatedExponentialOperation);
    }
    
    /**
     * DiscreteGamma operation implementation
     */
    private void discreteGammaOperation(Stack stack, Environment env) {
        // PhyloSpec: DiscreteGamma(shape: PositiveReal, categories: PosInteger) -> Vector<PositiveReal>
        Parameter categories = stack.pop(Parameter.class);
        Parameter shape = stack.pop(Parameter.class);
        
        // Create DiscreteGamma distribution
        DiscreteGamma dist = new DiscreteGamma(shape, categories);
        stack.push(dist);
    }
    
    /**
     * DiscreteGammaVector operation implementation
     */
    private void discreteGammaVectorOperation(Stack stack, Environment env) {
        // PhyloSpec: DiscreteGammaVector(shape: PositiveReal, categories: PosInteger, dimension: PosInteger) -> Vector<PositiveReal>
        Parameter dimension = stack.pop(Parameter.class);
        Parameter categories = stack.pop(Parameter.class);
        Parameter shape = stack.pop(Parameter.class);
        
        // Create DiscreteGammaVector distribution with dimension
        DiscreteGamma dist = new DiscreteGamma(shape, categories, dimension);
        stack.push(dist);
    }
    
    /**
     * FreeRates operation implementation
     */
    private void freeRatesOperation(Stack stack, Environment env) {
        // PhyloSpec: FreeRates(rates: Vector<PositiveReal>, weights: Simplex) -> Vector<PositiveReal>
        Parameter weights = stack.pop(Parameter.class);
        Parameter rates = stack.pop(Parameter.class);
        
        // FreeRates model (to be implemented)
        throw new UnsupportedOperationException("FreeRates model not yet implemented");
    }
    
    /**
     * InvariantSites operation implementation
     */
    private void invariantSitesOperation(Stack stack, Environment env) {
        // PhyloSpec: InvariantSites(proportion: Probability) -> Vector<Real>
        Parameter proportion = stack.pop(Parameter.class);
        
        // InvariantSites model (to be implemented)
        throw new UnsupportedOperationException("InvariantSites model not yet implemented");
    }
    
    /**
     * StrictClock operation implementation
     */
    private void strictClockOperation(Stack stack, Environment env) {
        // PhyloSpec: StrictClock(rate: PositiveReal) -> Vector<PositiveReal>
        Parameter rate = stack.pop(Parameter.class);
        
        // StrictClock model (to be implemented)
        throw new UnsupportedOperationException("StrictClock model not yet implemented");
    }
    
    /**
     * UncorrelatedLognormal operation implementation
     */
    private void uncorrelatedLognormalOperation(Stack stack, Environment env) {
        // PhyloSpec: UncorrelatedLognormal(mean: Real, stdev: PositiveReal) -> Vector<PositiveReal>
        Parameter stdev = stack.pop(Parameter.class);
        Parameter mean = stack.pop(Parameter.class);
        
        // UncorrelatedLognormal model (to be implemented)
        throw new UnsupportedOperationException("UncorrelatedLognormal model not yet implemented");
    }
    
    /**
     * UncorrelatedExponential operation implementation
     */
    private void uncorrelatedExponentialOperation(Stack stack, Environment env) {
        // PhyloSpec: UncorrelatedExponential(mean: PositiveReal) -> Vector<PositiveReal>
        Parameter mean = stack.pop(Parameter.class);
        
        // UncorrelatedExponential model (to be implemented)
        throw new UnsupportedOperationException("UncorrelatedExponential model not yet implemented");
    }
}