package io.github.stackphy.parser;

import java.util.List;

/**
 * The classic parser implementation for StackPhy.
 */
public class ClassicParser implements IStackPhyParser {
    private final String source;
    
    /**
     * Creates a new classic parser for the given source code.
     * 
     * @param source The source code to parse
     */
    public ClassicParser(String source) {
        this.source = source;
    }
    
    @Override
    public List<Operation> parse() throws StackPhyException {
        // Use the old lexer and parser to parse the source
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        // Use the updated Parser which now uses the OperationRegistry
        Parser parser = new Parser(tokens);
        return parser.parse();
    }
    
    @Override
    public String getSource() {
        return source;
    }
}