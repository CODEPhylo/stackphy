package io.github.stackphy.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer for the StackPhy language.
 * Converts source code into tokens.
 */
public class Lexer {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[+-]?([0-9]*[.])?[0-9]+");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"([^\"\\\\]|\\\\.)*\"");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("^\\s+");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^//.*");
    
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        // Operators
        KEYWORDS.put("var", TokenType.VAR);
        KEYWORDS.put("observe", TokenType.OBSERVE);
        
        // Base operations
        KEYWORDS.put("dup", TokenType.DUP);
        KEYWORDS.put("swap", TokenType.SWAP);
        KEYWORDS.put("drop", TokenType.DROP);
        
        // Distribution operations
        KEYWORDS.put("normal", TokenType.NORMAL);
        KEYWORDS.put("lognormal", TokenType.LOGNORMAL);
        KEYWORDS.put("exponential", TokenType.EXPONENTIAL);
        KEYWORDS.put("dirichlet", TokenType.DIRICHLET);
        KEYWORDS.put("gamma", TokenType.GAMMA);
        KEYWORDS.put("yule", TokenType.YULE);
        KEYWORDS.put("birthDeath", TokenType.BIRTH_DEATH);
        KEYWORDS.put("coalescent", TokenType.COALESCENT);
        
        // Model operations
        KEYWORDS.put("hky", TokenType.HKY);
        KEYWORDS.put("gtr", TokenType.GTR);
        KEYWORDS.put("phyloCTMC", TokenType.PHYLO_CTMC);
        
        // Data operations
        KEYWORDS.put("sequence", TokenType.SEQUENCE);
    }
    
    private final String source;
    private int position;
    private int line;
    private int column;
    
    /**
     * Creates a new lexer for the given source code.
     * 
     * @param source The source code
     */
    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }
    
    /**
     * Tokenizes the source code.
     * 
     * @return List of tokens
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (position < source.length()) {
            // Skip whitespace and comments
            if (skipWhitespaceAndComments()) {
                continue;
            }
            
            char c = peek();
            
            if (c == '[') {
                tokens.add(new Token(TokenType.BRACKET_OPEN, "[", line, column));
                advance();
            } else if (c == ']') {
                tokens.add(new Token(TokenType.BRACKET_CLOSE, "]", line, column));
                advance();
            } else if (c == '~') {
                tokens.add(new Token(TokenType.TILDE, "~", line, column));
                advance();
            } else if (c == '=') {
                tokens.add(new Token(TokenType.EQUAL, "=", line, column));
                advance();
            } else if (c == '"') {
                tokens.add(lexString());
            } else if (Character.isDigit(c) || c == '+' || c == '-' || c == '.') {
                tokens.add(lexNumber());
            } else if (Character.isLetter(c)) {
                tokens.add(lexIdentifier());
            } else {
                // Unexpected character
                tokens.add(new Token(TokenType.ERROR, String.valueOf(c), line, column));
                advance();
            }
        }
        
        // Add EOF token
        tokens.add(new Token(TokenType.EOF, "", line, column));
        
        return tokens;
    }
    
    /**
     * Skips whitespace and comments.
     * 
     * @return true if whitespace or comments were skipped, false otherwise
     */
    private boolean skipWhitespaceAndComments() {
        boolean skipped = false;
        
        while (position < source.length()) {
            String remaining = source.substring(position);
            
            Matcher whitespaceMatcher = WHITESPACE_PATTERN.matcher(remaining);
            if (whitespaceMatcher.find()) {
                String whitespace = whitespaceMatcher.group();
                for (int i = 0; i < whitespace.length(); i++) {
                    if (whitespace.charAt(i) == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                }
                position += whitespace.length();
                skipped = true;
                continue;
            }
            
            Matcher commentMatcher = COMMENT_PATTERN.matcher(remaining);
            if (commentMatcher.find()) {
                String comment = commentMatcher.group();
                position += comment.length();
                column += comment.length();
                skipped = true;
                continue;
            }
            
            break;
        }
        
        return skipped;
    }
    
    /**
     * Lexes a string literal.
     * 
     * @return The string token
     */
    private Token lexString() {
        int startColumn = column;
        String remaining = source.substring(position);
        
        Matcher matcher = STRING_PATTERN.matcher(remaining);
        if (matcher.find()) {
            String stringLiteral = matcher.group();
            position += stringLiteral.length();
            column += stringLiteral.length();
            
            // Remove the quotes from the value
            String value = stringLiteral.substring(1, stringLiteral.length() - 1);
            
            return new Token(TokenType.STRING, value, line, startColumn);
        } else {
            // Unterminated string
            while (position < source.length() && peek() != '\n') {
                advance();
            }
            
            return new Token(TokenType.ERROR, "Unterminated string", line, startColumn);
        }
    }
    
    /**
     * Lexes a number literal.
     * 
     * @return The number token
     */
    private Token lexNumber() {
        int startColumn = column;
        String remaining = source.substring(position);
        
        Matcher matcher = NUMBER_PATTERN.matcher(remaining);
        if (matcher.find()) {
            String numberLiteral = matcher.group();
            position += numberLiteral.length();
            column += numberLiteral.length();
            
            return new Token(TokenType.NUMBER, numberLiteral, line, startColumn);
        } else {
            // Invalid number
            char c = peek();
            advance();
            
            return new Token(TokenType.ERROR, String.valueOf(c), line, startColumn);
        }
    }
    
    /**
     * Lexes an identifier or keyword.
     * 
     * @return The identifier or keyword token
     */
    private Token lexIdentifier() {
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (position < source.length() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(peek());
            advance();
        }
        
        String word = sb.toString();
        
        // Check if it's a keyword
        if (KEYWORDS.containsKey(word)) {
            return new Token(KEYWORDS.get(word), word, line, startColumn);
        } else {
            // Unknown identifier, treat as error
            return new Token(TokenType.ERROR, word, line, startColumn);
        }
    }
    
    /**
     * Returns the current character without advancing.
     * 
     * @return The current character
     */
    private char peek() {
        return source.charAt(position);
    }
    
    /**
     * Advances to the next character.
     */
    private void advance() {
        position++;
        column++;
    }
}
