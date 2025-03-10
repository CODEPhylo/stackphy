package io.github.stackphy.parser;

import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Operation that executes a named operation.
 */
public class NamedOperation implements Operation {
    private final String name;
    private final OperationExecutor executor;
    private final int line;
    private final int column;
    
    /**
     * Creates a new named operation.
     * 
     * @param name The operation name
     * @param executor The operation executor
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public NamedOperation(String name, OperationExecutor executor, int line, int column) {
        this.name = name;
        this.executor = executor;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        try {
            executor.execute(stack, env);
        } catch (Exception e) {
            throw new StackPhyException("Error executing operation '" + name + "': " + e.getMessage(), e, line, column);
        }
    }
    
    /**
     * Gets the operation name.
     * 
     * @return The operation name
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("Operation(%s)", name);
    }
    
    /**
     * Functional interface for operation execution.
     */
    @FunctionalInterface
    public interface OperationExecutor {
        /**
         * Executes an operation on the given stack and environment.
         * 
         * @param stack The stack to operate on
         * @param env The environment to use
         * @throws Exception if an error occurs during execution
         */
        void execute(Stack stack, Environment env) throws Exception;
    }
}
