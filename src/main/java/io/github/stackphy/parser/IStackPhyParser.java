package io.github.stackphy.parser;

import java.util.List;

/**
 * Interface for StackPhy language parsers.
 * This provides a common interface for all parser implementations.
 */
public interface IStackPhyParser {
    
    /**
     * Parses the source code into a list of operations.
     * 
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    List<Operation> parse() throws StackPhyException;
    
    /**
     * Gets the source code being parsed.
     * 
     * @return The source code
     */
    String getSource();
}