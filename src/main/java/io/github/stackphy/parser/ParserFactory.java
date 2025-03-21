package io.github.stackphy.parser;

import io.github.stackphy.grammar.StackPhyLexer;
import io.github.stackphy.grammar.StackPhyParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

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
    private static ParserType defaultType = ParserType.CLASSIC;
    
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
     * Parses the given source code using the default parser type.
     * 
     * @param source The source code to parse
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    public static List<Operation> parse(String source) throws StackPhyException {
        return parse(source, defaultType);
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
        switch (type) {
            case ANTLR:
                return parseWithAntlr(source);
            case CLASSIC:
            default:
                return parseWithClassic(source);
        }
    }
    
    /**
     * Parses the given source code using the classic parser.
     * 
     * @param source The source code to parse
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    private static List<Operation> parseWithClassic(String source) throws StackPhyException {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
    
    /**
     * Parses the given source code using the ANTLR parser.
     * 
     * @param source The source code to parse
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    private static List<Operation> parseWithAntlr(String source) throws StackPhyException {
        try {
            // Create lexer and parser
            StackPhyLexer lexer = new StackPhyLexer(CharStreams.fromString(source));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            StackPhyParser parser = new StackPhyParser(tokens);
            
            // Add error listeners if needed
            // parser.removeErrorListeners();
            // parser.addErrorListener(new StackPhyErrorListener());
            
            // Parse the program
            ParseTree tree = parser.program();
            
            // Create visitor to convert parse tree to operations
            StackPhyVisitor visitor = new StackPhyVisitor();
            return visitor.visit(tree);
            
        } catch (Exception e) {
            throw new StackPhyException("ANTLR parsing error: " + e.getMessage(), e, 0, 0);
        }
    }
}