// Operation.java
package io.github.stackphy.parser;

import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Base interface for all operations in the StackPhy language.
 */
public interface Operation {
    /**
     * Executes this operation on the given stack and environment.
     * 
     * @param stack The stack to operate on
     * @param env The environment to use
     * @throws StackPhyException if an error occurs during execution
     */
    void execute(Stack stack, Environment env) throws StackPhyException;
    
    /**
     * Gets the line number where this operation was found.
     * 
     * @return The line number
     */
    int getLine();
    
    /**
     * Gets the column number where this operation was found.
     * 
     * @return The column number
     */
    int getColumn();
    
    /**
     * Returns a string representation of this operation.
     * 
     * @return A string representation
     */
    String toString();
}