package io.github.stackphy.parser;

import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

/**
 * Operation that represents a stack comment in a function definition.
 */
public class StackCommentOperation extends AbstractOperation {
    private final String comment;
    
    /**
     * Creates a new stack comment operation.
     * 
     * @param comment The stack comment
     * @param line The line number where the operation was found
     * @param column The column number where the operation was found
     */
    public StackCommentOperation(String comment, int line, int column) {
        super(line, column);
        this.comment = comment;
    }
    
    @Override
    public void execute(Stack stack, Environment env) throws StackPhyException {
        // Stack comment is handled specially by the interpreter
        // This should not be executed directly
        throw new StackPhyException("Stack comment operation should not be executed directly", getLine(), getColumn());
    }
    
    /**
     * Gets the stack comment.
     * 
     * @return The stack comment
     */
    public String getComment() {
        return comment;
    }
    
    @Override
    public String toString() {
        return String.format("StackComment(%s)", comment);
    }
}