package io.github.stackphy.parser;

import io.github.stackphy.grammar.StackPhyLexer;
import io.github.stackphy.grammar.StackPhyParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser implementation that uses ANTLR to parse StackPhy code.
 */
public class AntlrParser {
    private final String source;
    
    /**
     * Creates a new ANTLR-based parser for the given source code.
     * 
     * @param source The source code to parse
     */
    public AntlrParser(String source) {
        this.source = source;
    }
    
    /**
     * Parses the source code into a list of operations.
     * 
     * @return The list of operations
     * @throws StackPhyException if a parsing error occurs
     */
    public List<Operation> parse() throws StackPhyException {
        try {
            // Create lexer and parser
            StackPhyLexer lexer = new StackPhyLexer(CharStreams.fromString(source));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            StackPhyParser parser = new StackPhyParser(tokens);
            
            // Add error listeners if needed
            // parser.removeErrorListeners();
            // parser.addErrorListener(new CustomErrorListener());
            
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