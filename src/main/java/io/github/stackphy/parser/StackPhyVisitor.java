package io.github.stackphy.parser;

import io.github.stackphy.grammar.StackPhyBaseVisitor;
import io.github.stackphy.grammar.StackPhyParser;
import io.github.stackphy.model.UserFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visitor that converts an ANTLR parse tree into StackPhy operations.
 */
public class StackPhyVisitor extends StackPhyBaseVisitor<List<Operation>> {
    // Keep track of the current function being defined
    private String currentFunction = null;
    
    // Keep track of the parameters for each function
    private final Map<String, Set<String>> functionParameters = new java.util.HashMap<>();
    
    // Operation registry for creating named operations
    private final OperationRegistry registry = OperationRegistry.getInstance();
    
    @Override
    public List<Operation> visitProgram(StackPhyParser.ProgramContext ctx) {
        List<Operation> operations = new ArrayList<>();
        
        // Process each statement
        for (StackPhyParser.StatementContext statementCtx : ctx.statement()) {
            List<Operation> statementOps = visit(statementCtx);
            if (statementOps != null) {
                operations.addAll(statementOps);
            }
        }
        
        return operations;
    }
    
    @Override
    public List<Operation> visitStatement(StackPhyParser.StatementContext ctx) {
        // Delegate to appropriate visit method based on type of statement
        if (ctx.functionDefinition() != null) {
            return visit(ctx.functionDefinition());
        } else if (ctx.operation() != null) {
            return visit(ctx.operation());
        } else if (ctx.value() != null) {
            return visit(ctx.value());
        } else if (ctx.comment() != null) {
            // Comments are ignored for execution
            return new ArrayList<>();
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public List<Operation> visitFunctionDefinition(StackPhyParser.FunctionDefinitionContext ctx) {
        List<Operation> operations = new ArrayList<>();
        
        // Get the line and column info
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Add function start operation
        operations.add(new FunctionStartOperation(line, column));
        
        // Add function name operation
        String functionName = ctx.IDENTIFIER().getText();
        currentFunction = functionName;
        operations.add(new FunctionNameOperation(functionName, line, column));
        
        // Extract parameters from stack effect if present
        Set<String> parameters = new HashSet<>();
        if (ctx.stackEffect() != null) {
            // Create a string representation of the stack effect for compatibility
            StringBuilder stackEffectText = new StringBuilder();
            
            // Get input parameters
            if (ctx.stackEffect().inputParams() != null) {
                for (int i = 0; i < ctx.stackEffect().inputParams().IDENTIFIER().size(); i++) {
                    String paramName = ctx.stackEffect().inputParams().IDENTIFIER(i).getText();
                    parameters.add(paramName);
                    stackEffectText.append(paramName).append(" ");
                }
            }
            
            stackEffectText.append("-- ");
            
            // Get output description
            if (ctx.stackEffect().outputDescription() != null) {
                stackEffectText.append(ctx.stackEffect().outputDescription().getText());
            }
            
            // Create stack comment operation
            operations.add(new StackCommentOperation(stackEffectText.toString().trim(), 
                          ctx.stackEffect().getStart().getLine(), 
                          ctx.stackEffect().getStart().getCharPositionInLine()));
        }
        
        // Store parameters for this function
        functionParameters.put(functionName, parameters);
        
        // Process function body statements
        for (StackPhyParser.StatementContext statementCtx : ctx.statement()) {
            List<Operation> statementOps = visit(statementCtx);
            if (statementOps != null) {
                operations.addAll(statementOps);
            }
        }
        
        // Add function end operation
        operations.add(new FunctionEndOperation(
                ctx.getStop().getLine(), 
                ctx.getStop().getCharPositionInLine()));
        
        // Clear current function after processing
        currentFunction = null;
        
        return operations;
    }
    
    @Override
    public List<Operation> visitOperation(StackPhyParser.OperationContext ctx) {
        List<Operation> operations = new ArrayList<>();
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        try {
            // Handle different types of operations
            if (ctx.dup() != null) {
                // Create a named operation for dup
                operations.add(registry.createNamedOperation("dup", line, column));
            } else if (ctx.swap() != null) {
                operations.add(registry.createNamedOperation("swap", line, column));
            } else if (ctx.rot() != null) {
                operations.add(registry.createNamedOperation("rot", line, column));
            } else if (ctx.pick() != null) {
                operations.add(registry.createNamedOperation("pick", line, column));
            } else if (ctx.drop() != null) {
                operations.add(registry.createNamedOperation("drop", line, column));
            } else if (ctx.over() != null) {
                operations.add(registry.createNamedOperation("over", line, column));
            } else if (ctx.getText().equals("~")) {
                operations.add(registry.createNamedOperation("~", line, column));
            } else if (ctx.getText().equals("=")) {
                operations.add(registry.createNamedOperation("=", line, column));
            } else if (ctx.var() != null) {
                operations.add(registry.createNamedOperation("var", line, column));
            } else if (ctx.observe() != null) {
                operations.add(registry.createNamedOperation("observe", line, column));
            } else if (ctx.getText().equals("exp")) {
                operations.add(registry.createNamedOperation("exp", line, column));
            } else if (ctx.getText().equals("pi")) {
                operations.add(registry.createNamedOperation("pi", line, column));
            } else if (ctx.getText().equals("sqrt")) {
                operations.add(registry.createNamedOperation("sqrt", line, column));
            } else if (ctx.getText().equals("negate")) {
                operations.add(registry.createNamedOperation("negate", line, column));
            } else if (ctx.getText().equals("*")) {
                operations.add(registry.createNamedOperation("*", line, column));
            } else if (ctx.getText().equals("+")) {
                operations.add(registry.createNamedOperation("+", line, column));
            } else if (ctx.getText().equals("-")) {
                operations.add(registry.createNamedOperation("-", line, column));
            } else if (ctx.getText().equals("/")) {
                operations.add(registry.createNamedOperation("/", line, column));
            } else if (ctx.mathOperation() != null) {
                // Handle math operations
                String opName = ctx.mathOperation().getText();
                operations.add(registry.createNamedOperation(opName, line, column));
            } else if (ctx.distributionOperation() != null) {
                // Handle distribution operations
                String opName = ctx.distributionOperation().getText();
                operations.add(registry.createNamedOperation(opName.toLowerCase(), line, column));
            } else if (ctx.arrayOperation() != null) {
                // Handle array operations
                String opName = ctx.arrayOperation().getText();
                if (opName.equals("[")) {
                    operations.add(new ValueOperation("ARRAY_MARKER", line, column));
                } else if (opName.equals("]")) {
                    operations.add(registry.createNamedOperation("]", line, column));
                }
            } else if (ctx.IDENTIFIER() != null) {
                // Check if it's a variable/parameter reference in a function definition
                String name = ctx.IDENTIFIER().getText();
                
                if (currentFunction != null && 
                    functionParameters.containsKey(currentFunction) && 
                    functionParameters.get(currentFunction).contains(name)) {
                    
                    // It's a parameter reference in a function body
                    operations.add(new ValueOperation(name, line, column));
                    operations.add(registry.createNamedOperation("var", line, column));
                } else {
                    // Otherwise it's a function call or global variable reference
                    operations.add(new FunctionCallUserOperation(name, line, column));
                }
            }
        } catch (StackPhyException e) {
            // Log the error but continue parsing
            System.err.println("Error creating operation: " + e.getMessage());
        }
        
        return operations;
    }
    
    @Override
    public List<Operation> visitValue(StackPhyParser.ValueContext ctx) {
        List<Operation> operations = new ArrayList<>();
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        if (ctx.NUMBER() != null) {
            // Parse number
            String numberText = ctx.NUMBER().getText();
            double value = Double.parseDouble(numberText);
            operations.add(new ValueOperation(value, line, column));
        } else if (ctx.STRING() != null) {
            // Parse string (remove quotes)
            String stringText = ctx.STRING().getText();
            String value = stringText.substring(1, stringText.length() - 1);
            operations.add(new ValueOperation(value, line, column));
        }
        
        return operations;
    }
}