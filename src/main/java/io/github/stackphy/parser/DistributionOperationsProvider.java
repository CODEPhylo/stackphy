package io.github.stackphy.parser;

import io.github.stackphy.distribution.*;
import io.github.stackphy.functions.DistributionRegistry;
import io.github.stackphy.functions.DistributionSignature;
import io.github.stackphy.model.Parameter;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for distribution operations.
 */
public class DistributionOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        // Core continuous distributions
        operations.put("normal", this::normalDistribution);
        operations.put("lognormal", this::lognormalDistribution);
        operations.put("exponential", this::exponentialDistribution);
        operations.put("gamma", this::gammaDistribution);
        operations.put("beta", this::betaDistribution);
        operations.put("dirichlet", this::dirichletDistribution);
        operations.put("uniform", this::uniformDistribution);
        
        // Tree distributions
        operations.put("yule", this::yuleDistribution);
        operations.put("birthdeath", this::birthdeathDistribution);
        operations.put("coalescent", this::coalescentDistribution);
        operations.put("fossilbirthdeath", this::fossilBirthDeathDistribution);
    }
    
    /**
     * Normal distribution implementation
     */
    private void normalDistribution(Stack stack, Environment env) {
        // Check against PhyloSpec signature
        DistributionSignature signature = DistributionRegistry.getDistribution("Normal");
        
        Parameter sd = stack.pop(Parameter.class);
        Parameter mean = stack.pop(Parameter.class);
        
        // Validate types (to be implemented)
        
        Normal dist = new Normal(mean, sd);
        stack.push(dist);
    }
    
    /**
     * LogNormal distribution implementation
     */
    private void lognormalDistribution(Stack stack, Environment env) {
        // PhyloSpec: LogNormal(meanlog: Real, sdlog: PositiveReal) -> PositiveReal
        Parameter sdlog = stack.pop(Parameter.class);
        Parameter meanlog = stack.pop(Parameter.class);
        
        LogNormal dist = new LogNormal(meanlog, sdlog);
        stack.push(dist);
    }
    
    /**
     * Exponential distribution implementation
     */
    private void exponentialDistribution(Stack stack, Environment env) {
        // PhyloSpec: Exponential(rate: PositiveReal) -> PositiveReal
        Parameter rate = stack.pop(Parameter.class);
        
        Exponential dist = new Exponential(rate);
        stack.push(dist);
    }
    
    /**
     * Gamma distribution implementation
     */
    private void gammaDistribution(Stack stack, Environment env) {
        // PhyloSpec: Gamma(shape: PositiveReal, rate: PositiveReal) -> PositiveReal
        Parameter rate = stack.pop(Parameter.class);
        Parameter shape = stack.pop(Parameter.class);
        
        Gamma dist = new Gamma(shape, rate);
        stack.push(dist);
    }
    
    /**
     * Beta distribution implementation
     */
    private void betaDistribution(Stack stack, Environment env) {
        // PhyloSpec: Beta(alpha: PositiveReal, beta: PositiveReal) -> Probability
        Parameter beta = stack.pop(Parameter.class);
        Parameter alpha = stack.pop(Parameter.class);
        
        // Beta distribution (to be implemented)
        throw new UnsupportedOperationException("Beta distribution not yet implemented");
    }
    
    /**
     * Dirichlet distribution implementation
     */
    private void dirichletDistribution(Stack stack, Environment env) {
        // PhyloSpec: Dirichlet(alpha: Vector<PositiveReal>) -> Simplex
        Parameter concentrationParams = stack.pop(Parameter.class);
        
        Dirichlet dist = new Dirichlet(concentrationParams);
        stack.push(dist);
    }
    
    /**
     * Uniform distribution implementation
     */
    private void uniformDistribution(Stack stack, Environment env) {
        // PhyloSpec: Uniform(lower: Real, upper: Real) -> Real
        Parameter upper = stack.pop(Parameter.class);
        Parameter lower = stack.pop(Parameter.class);
        
        // Uniform distribution (to be implemented)
        throw new UnsupportedOperationException("Uniform distribution not yet implemented");
    }
    
    /**
     * Yule distribution implementation
     */
    private void yuleDistribution(Stack stack, Environment env) {
        // PhyloSpec: Yule(birthRate: PositiveReal) -> Tree
        Parameter birthRate = stack.pop(Parameter.class);
        
        // Create a Yule process with the birth rate
        Yule yule = new Yule(birthRate);
        stack.push(yule);
    }
    
    /**
     * Birth-Death distribution implementation
     */
    private void birthdeathDistribution(Stack stack, Environment env) {
        // PhyloSpec: BirthDeath(birthRate: PositiveReal, deathRate: PositiveReal, rootHeight: PositiveReal?) -> Tree
        Parameter deathRate = stack.pop(Parameter.class);
        Parameter birthRate = stack.pop(Parameter.class);
        
        // For now, just use the constructor with required parameters
        BirthDeath dist = new BirthDeath(birthRate, deathRate);
        stack.push(dist);
    }
    
    /**
     * Coalescent distribution implementation
     */
    private void coalescentDistribution(Stack stack, Environment env) {
        // PhyloSpec: Coalescent(populationSize: PositiveReal) -> Tree
        Parameter popSize = stack.pop(Parameter.class);
        
        Coalescent dist = new Coalescent(popSize);
        stack.push(dist);
    }
    
    /**
     * Fossil Birth-Death distribution implementation
     */
    private void fossilBirthDeathDistribution(Stack stack, Environment env) {
        // PhyloSpec: FossilBirthDeath(birthRate: PositiveReal, deathRate: PositiveReal, samplingRate: PositiveReal, rho: Probability) -> TimeTree
        Parameter rho = stack.pop(Parameter.class);
        Parameter samplingRate = stack.pop(Parameter.class);
        Parameter deathRate = stack.pop(Parameter.class);
        Parameter birthRate = stack.pop(Parameter.class);
        
        // FossilBirthDeath distribution (to be implemented)
        throw new UnsupportedOperationException("FossilBirthDeath process not yet implemented");
    }
}