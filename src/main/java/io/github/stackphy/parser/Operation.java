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
     * Returns a string representation of this operation.
     * 
     * @return A string representation
     */
    String toString();
}
