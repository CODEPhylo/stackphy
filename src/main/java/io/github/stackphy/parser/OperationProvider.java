package io.github.stackphy.parser;

import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;
import java.util.Map;

/**
 * Interface for classes that provide operations for the StackPhy language.
 */
public interface OperationProvider {
    
    /**
     * Registers operations with the provided map.
     * 
     * @param operations The map to register operations with
     */
    void registerOperations(Map<String, NamedOperation.OperationExecutor> operations);
}