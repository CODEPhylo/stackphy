package io.github.stackphy.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Custom error listener for ANTLR parser.
 */
public class StackPhyErrorListener extends BaseErrorListener {
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                            int line, int charPositionInLine, 
                            String msg, RecognitionException e) {
        String error = String.format("Syntax error at line %d:%d - %s", 
                                    line, charPositionInLine, msg);
        
        throw new ParseCancellationException(new StackPhyException(error, line, charPositionInLine));
    }
}