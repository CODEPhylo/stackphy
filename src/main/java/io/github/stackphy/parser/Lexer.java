package io.github.stackphy.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer for the PhyloSpec-compatible StackPhy language.
 * Converts source code into tokens.
 */
public class Lexer {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[+-]?([0-9]*[.])?[0-9]+([eE][+-]?[0-9]+)?");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"([^\"\\\\]|\\\\.)*\"");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("^\\s+");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^//.*");
    
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        // Variable operations
        KEYWORDS.put("var", TokenType.VAR);
        KEYWORDS.put("observe", TokenType.OBSERVE);
        KEYWORDS.put("constraint", TokenType.CONSTRAINT);
        
        // Base operations
        KEYWORDS.put("dup", TokenType.DUP);
        KEYWORDS.put("swap", TokenType.SWAP);
        KEYWORDS.put("drop", TokenType.DROP);
        
        // Constraint types
        KEYWORDS.put("lessThan", TokenType.LESS_THAN);
        KEYWORDS.put("LessThan", TokenType.LESS_THAN);
        KEYWORDS.put("greaterThan", TokenType.GREATER_THAN);
        KEYWORDS.put("GreaterThan", TokenType.GREATER_THAN);
        KEYWORDS.put("equals", TokenType.EQUALS);
        KEYWORDS.put("Equals", TokenType.EQUALS);
        KEYWORDS.put("bounded", TokenType.BOUNDED);
        KEYWORDS.put("Bounded", TokenType.BOUNDED);
        KEYWORDS.put("sumTo", TokenType.SUM_TO);
        KEYWORDS.put("SumTo", TokenType.SUM_TO);
        KEYWORDS.put("monophyly", TokenType.MONOPHYLY);
        KEYWORDS.put("Monophyly", TokenType.MONOPHYLY);
        KEYWORDS.put("calibration", TokenType.CALIBRATION);
        KEYWORDS.put("Calibration", TokenType.CALIBRATION);
        
        // Distribution operations
        KEYWORDS.put("Normal", TokenType.NORMAL);
        KEYWORDS.put("LogNormal", TokenType.LOGNORMAL);
        KEYWORDS.put("Exponential", TokenType.EXPONENTIAL);
        KEYWORDS.put("Gamma", TokenType.GAMMA);
        KEYWORDS.put("Beta", TokenType.BETA);
        KEYWORDS.put("Dirichlet", TokenType.DIRICHLET);
        KEYWORDS.put("Uniform", TokenType.UNIFORM);
        
        // Tree distributions
        KEYWORDS.put("Yule", TokenType.YULE);
        KEYWORDS.put("BirthDeath", TokenType.BIRTH_DEATH);
        KEYWORDS.put("Coalescent", TokenType.COALESCENT);
        KEYWORDS.put("FossilBirthDeath", TokenType.FOSSIL_BIRTH_DEATH);
        
        // Substitution models
        KEYWORDS.put("JC69", TokenType.JC69);
        KEYWORDS.put("K80", TokenType.K80);
        KEYWORDS.put("F81", TokenType.F81);
        KEYWORDS.put("HKY", TokenType.HKY);
        KEYWORDS.put("GTR", TokenType.GTR);
        KEYWORDS.put("WAG", TokenType.WAG);
        KEYWORDS.put("JTT", TokenType.JTT);
        KEYWORDS.put("LG", TokenType.LG);
        KEYWORDS.put("GY94", TokenType.GY94);
        
        // Rate heterogeneity
        KEYWORDS.put("DiscreteGamma", TokenType.DISCRETE_GAMMA);
        KEYWORDS.put("DiscreteGammaVector", TokenType.DISCRETE_GAMMA_VECTOR);
        KEYWORDS.put("FreeRates", TokenType.FREE_RATES);
        KEYWORDS.put("InvariantSites", TokenType.INVARIANT_SITES);
        KEYWORDS.put("StrictClock", TokenType.STRICT_CLOCK);
        KEYWORDS.put("UncorrelatedLognormal", TokenType.RELAXED_LOGNORMAL);
        KEYWORDS.put("UncorrelatedExponential", TokenType.RELAXED_EXPONENTIAL);
        
        // Tree operations
        KEYWORDS.put("mrca", TokenType.MRCA);
        KEYWORDS.put("treeHeight", TokenType.TREE_HEIGHT);
        KEYWORDS.put("nodeAge", TokenType.NODE_AGE);
        KEYWORDS.put("branchLength", TokenType.BRANCH_LENGTH);
        KEYWORDS.put("distanceMatrix", TokenType.DISTANCE_MATRIX);
        KEYWORDS.put("descendantTaxa", TokenType.DESCENDANT_TAXA);
        
        // Phylogenetic processes
        KEYWORDS.put("PhyloCTMC", TokenType.PHYLO_CTMC);
        KEYWORDS.put("PhyloBM", TokenType.PHYLO_BM);
        KEYWORDS.put("PhyloOU", TokenType.PHYLO_OU);
        
        // Mixture models
        KEYWORDS.put("Mixture", TokenType.MIXTURE);
        KEYWORDS.put("DiscreteGammaMixture", TokenType.DISCRETE_GAMMA_MIXTURE);
        
        // Math operations
        KEYWORDS.put("vectorElement", TokenType.VECTOR_ELEMENT);
        KEYWORDS.put("matrixElement", TokenType.MATRIX_ELEMENT);
        KEYWORDS.put("scale", TokenType.SCALE);
        KEYWORDS.put("normalize", TokenType.NORMALIZE);
        KEYWORDS.put("log", TokenType.LOG);
        KEYWORDS.put("exp", TokenType.EXP);
        KEYWORDS.put("sum", TokenType.SUM);
        KEYWORDS.put("product", TokenType.PRODUCT);
        
        // Sequence operations
        KEYWORDS.put("sequence", TokenType.SEQUENCE);
        KEYWORDS.put("alignment", TokenType.ALIGNMENT);
    }
    
    private final String source;
    private int position;
    private int line;
    private int column;
    private boolean inFunctionDefinition = false;
   
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
            } else if (c == '(') {
                tokens.add(new Token(TokenType.PAREN_OPEN, "(", line, column));
                advance();
            } else if (c == ')') {
                tokens.add(new Token(TokenType.PAREN_CLOSE, ")", line, column));
                advance();
            } else if (c == ',') {
                tokens.add(new Token(TokenType.COMMA, ",", line, column));
                advance();
            } else if (c == ':') {
                // This is a function definition start
                tokens.add(new Token(TokenType.FUNCTION_START, ":", line, column));
                inFunctionDefinition = true;
                advance();
            } else if (c == ';') {
                // This is a function definition end
                tokens.add(new Token(TokenType.FUNCTION_END, ";", line, column));
                inFunctionDefinition = false;
                advance();
            } else if (c == '*') {
                tokens.add(new Token(TokenType.MULTIPLY, "*", line, column));
                advance();
            } else if (c == '~') {
                tokens.add(new Token(TokenType.TILDE, "~", line, column));
                advance();
            } else if (c == '=') {
                tokens.add(new Token(TokenType.EQUAL, "=", line, column));
                advance();
            } else if (c == '"') {
                tokens.add(lexString());
            } else if (c == '-') {
                // Check if next char is a digit (negative number)
                if (position + 1 < source.length() && Character.isDigit(source.charAt(position + 1))) {
                    tokens.add(lexNumber());
                } else {
                    // It's just a dash token
                    tokens.add(new Token(TokenType.DASH, "-", line, column));
                    advance();
                }
            }
            else if (Character.isDigit(c) || c == '+' || c == '.') {
                tokens.add(lexNumber());
            } else if (Character.isLetter(c) || c == '_') {
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
        String remaining = source.substring(position);
        
        Matcher matcher = IDENTIFIER_PATTERN.matcher(remaining);
        if (matcher.find()) {
            String identifier = matcher.group();
            position += identifier.length();
            column += identifier.length();
            
            // Check if it's a keyword
            if (KEYWORDS.containsKey(identifier)) {
                return new Token(KEYWORDS.get(identifier), identifier, line, startColumn);
            } else {
                // Valid identifier
                return new Token(TokenType.IDENTIFIER, identifier, line, startColumn);
            }
        } else {
            // This should not happen due to the regex pattern
            char c = peek();
            advance();
            
            return new Token(TokenType.ERROR, String.valueOf(c), line, startColumn);
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