package io.github.stackphy.parser;

/**
 * Exception thrown by StackPhy operations.
 */
public class StackPhyException extends Exception {
    private final int line;
    private final int column;
    
    /**
     * Creates a new StackPhy exception.
     * 
     * @param message The error message
     * @param line The line number where the error occurred
     * @param column The column number where the error occurred
     */
    public StackPhyException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }
    
    /**
     * Creates a new StackPhy exception with a cause.
     * 
     * @param message The error message
     * @param cause The cause of the error
     * @param line The line number where the error occurred
     * @param column The column number where the error occurred
     */
    public StackPhyException(String message, Throwable cause, int line, int column) {
        super(message, cause);
        this.line = line;
        this.column = column;
    }
    
    /**
     * Gets the line number where the error occurred.
     * 
     * @return The line number
     */
    public int getLine() {
        return line;
    }
    
    /**
     * Gets the column number where the error occurred.
     * 
     * @return The column number
     */
    public int getColumn() {
        return column;
    }
    
    @Override
    public String getMessage() {
        return String.format("%s at %d:%d", super.getMessage(), line, column);
    }
}
