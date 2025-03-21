package io.github.stackphy.parser;

/**
 * Abstract base class for operations that provides common implementation.
 */
public abstract class AbstractOperation implements Operation {
    protected final int line;
    protected final int column;
    
    /**
     * Creates a new abstract operation.
     * 
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public AbstractOperation(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    @Override
    public int getLine() {
        return line;
    }
    
    @Override
    public int getColumn() {
        return column;
    }
}