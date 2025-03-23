package io.github.stackphy.parser;

import io.github.stackphy.grammar.StackPhyLexer;
import io.github.stackphy.grammar.StackPhyParser.ProgramContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * ANTLR-based parser implementation for StackPhy.
 */
public class AntlrParserImpl implements IStackPhyParser {
    private final String source;
    
    /**
     * Creates a new ANTLR-based parser for the given source code.
     * 
     * @param source The source code to parse
     */
    public AntlrParserImpl(String source) {
        this.source = source;
    }
    
    @Override
    public List<Operation> parse() throws StackPhyException {
        try {
            // Create lexer and parser
            StackPhyLexer lexer = new StackPhyLexer(CharStreams.fromString(source));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            io.github.stackphy.grammar.StackPhyParser parser = new io.github.stackphy.grammar.StackPhyParser(tokens);
            
            // Add error listeners
            parser.removeErrorListeners();
            parser.addErrorListener(new StackPhyErrorListener());
            
            // Parse the program
            ProgramContext tree = parser.program();
            
            // Create visitor to convert parse tree to operations
            StackPhyVisitor visitor = new StackPhyVisitor();
            return visitor.visit(tree);
            
        } catch (Exception e) {
            throw new StackPhyException("ANTLR parsing error: " + e.getMessage(), e, 0, 0);
        }
    }
    
    @Override
    public String getSource() {
        return source;
    }
}