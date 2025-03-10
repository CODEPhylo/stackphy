package io.github.stackphy.parser;

/**
 * Represents a token in the StackPhy language.
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;
    
    /**
     * Creates a new token.
     * 
     * @param type The token type
     * @param value The token value
     * @param line The line number where the token was found
     * @param column The column number where the token was found
     */
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    /**
     * Gets the token type.
     * 
     * @return The token type
     */
    public TokenType getType() {
        return type;
    }
    
    /**
     * Gets the token value.
     * 
     * @return The token value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Gets the line number where the token was found.
     * 
     * @return The line number
     */
    public int getLine() {
        return line;
    }
    
    /**
     * Gets the column number where the token was found.
     * 
     * @return The column number
     */
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s) at %d:%d", type, value, line, column);
    }
}
