package io.github.stackphy.parser;

import java.util.List;

/**
 * Factory for creating parsers.
 */
public class ParserFactory {
    
    /**
     * Parser type enum.
     */
    public enum ParserType {
        CLASSIC,    // The original Parser implementation
        ANTLR       // The ANTLR-based parser
    }
    
    /**
     * The default parser type to use.
     */
    private static ParserType defaultType = ParserType.ANTLR;
    
    /**
     * Sets the default parser type.
     * 
     * @param type The parser type to use by default
     */
    public static void setDefaultType(ParserType type) {
        defaultType = type;
    }
    
    /**
     * Gets the default parser type.
     * 
     * @return The default parser type
     */
    public static ParserType getDefaultType() {
        return defaultType;
    }
    
    /**
     * Creates a parser of the default type.
     * 
     * @param source The source code to parse
     * @return The parser
     */
    public static IStackPhyParser createParser(String source) {
        return createParser(source, defaultType);
    }
    
    /**
     * Creates a parser of the specified type.
     * 
     * @param source The source code to parse
     * @param type The parser type
     * @return The parser
     */
    public static IStackPhyParser createParser(String source, ParserType type) {
        switch (type) {
            case ANTLR:
                return new AntlrParserImpl(source);
            case CLASSIC:
            default:
                return new ClassicParser(source);
        }
    }
    
    /**
     * Parses the given source code using the default parser type.
     * 
     * @param source The source code to parse
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    public static List<Operation> parse(String source) throws StackPhyException {
        return createParser(source).parse();
    }
    
    /**
     * Parses the given source code using the specified parser type.
     * 
     * @param source The source code to parse
     * @param type The parser type to use
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    public static List<Operation> parse(String source, ParserType type) throws StackPhyException {
        return createParser(source, type).parse();
    }
}