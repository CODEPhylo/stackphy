package io.github.stackphy.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for operations in the StackPhy language.
 * This class centralizes all operation implementations to be used by any parser implementation.
 */
public class OperationRegistry {
    // Singleton instance
    private static OperationRegistry instance;
    
    // Map of operation names to their executors
    private final Map<String, NamedOperation.OperationExecutor> operations;
    
    // List of operation providers
    private final List<OperationProvider> providers;
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private OperationRegistry() {
        this.operations = new HashMap<>();
        this.providers = new ArrayList<>();
        
        // Register operation providers
        registerProviders();
        
        // Register all operations from providers
        for (OperationProvider provider : providers) {
            provider.registerOperations(operations);
        }
    }
    
    /**
     * Gets the singleton instance of the registry.
     * 
     * @return The registry instance
     */
    public static synchronized OperationRegistry getInstance() {
        if (instance == null) {
            instance = new OperationRegistry();
        }
        return instance;
    }
    
    /**
     * Gets the operation executor for the given name.
     * 
     * @param name The operation name
     * @return The operation executor, or null if not found
     */
    public NamedOperation.OperationExecutor getOperationExecutor(String name) {
        return operations.get(name.toLowerCase());
    }
    
    /**
     * Checks if an operation with the given name exists.
     * 
     * @param name The operation name
     * @return true if the operation exists, false otherwise
     */
    public boolean hasOperation(String name) {
        return operations.containsKey(name.toLowerCase());
    }
    
    /**
     * Creates a named operation with the given name.
     * 
     * @param name The operation name
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     * @return The named operation
     * @throws StackPhyException if the operation is not found
     */
    public NamedOperation createNamedOperation(String name, int line, int column) throws StackPhyException {
        String lowercaseName = name.toLowerCase();
        NamedOperation.OperationExecutor executor = operations.get(lowercaseName);
        
        if (executor == null) {
            throw new StackPhyException("Unknown operation: " + name, line, column);
        }
        
        return new NamedOperation(name, executor, line, column);
    }
    
    /**
     * Registers all operation providers.
     */
    private void registerProviders() {
        // Register all operation providers
        providers.add(new BaseOperationsProvider());
        providers.add(new MathOperationsProvider());
        providers.add(new ArrayOperationsProvider());
        providers.add(new VariableOperationsProvider());
        providers.add(new DistributionOperationsProvider());
        providers.add(new SubstitutionModelOperationsProvider());
        providers.add(new RateHeterogeneityOperationsProvider());
        providers.add(new TreeOperationsProvider());
        providers.add(new PhylogeneticProcessOperationsProvider());
        providers.add(new SequenceOperationsProvider());
        providers.add(new ConstraintOperationsProvider());
    }
}