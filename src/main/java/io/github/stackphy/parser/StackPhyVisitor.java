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
        
        // Handle different types of operations
        if (ctx.dup() != null) {
            // Create a named operation for dup
            operations.add(createNamedOperation("dup", line, column));
        } else if (ctx.swap() != null) {
            operations.add(createNamedOperation("swap", line, column));
        } else if (ctx.rot() != null) {
            operations.add(createNamedOperation("rot", line, column));
        } else if (ctx.pick() != null) {
            operations.add(createNamedOperation("pick", line, column));
        } else if (ctx.drop() != null) {
            operations.add(createNamedOperation("drop", line, column));
        } else if (ctx.over() != null) {
            operations.add(createNamedOperation("over", line, column));
        } else if (ctx.getText().equals("~")) {
            operations.add(createNamedOperation("~", line, column));
        } else if (ctx.getText().equals("=")) {
            operations.add(createNamedOperation("=", line, column));
        } else if (ctx.var() != null) {
            operations.add(createNamedOperation("var", line, column));
        } else if (ctx.observe() != null) {
            operations.add(createNamedOperation("observe", line, column));
        } else if (ctx.getText().equals("exp")) {
            operations.add(createNamedOperation("exp", line, column));
        } else if (ctx.getText().equals("pi")) {
            operations.add(createNamedOperation("pi", line, column));
        } else if (ctx.getText().equals("sqrt")) {
            operations.add(createNamedOperation("sqrt", line, column));
        } else if (ctx.getText().equals("negate")) {
            operations.add(createNamedOperation("negate", line, column));
        } else if (ctx.getText().equals("*")) {
            operations.add(createNamedOperation("*", line, column));
        } else if (ctx.getText().equals("+")) {
            operations.add(createNamedOperation("+", line, column));
        } else if (ctx.getText().equals("-")) {
            operations.add(createNamedOperation("-", line, column));
        } else if (ctx.getText().equals("/")) {
            operations.add(createNamedOperation("/", line, column));
        } else if (ctx.mathOperation() != null) {
            // Handle math operations
            String opName = ctx.mathOperation().getText();
            operations.add(createNamedOperation(opName, line, column));
        } else if (ctx.distributionOperation() != null) {
            // Handle distribution operations
            String opName = ctx.distributionOperation().getText();
            operations.add(createNamedOperation(opName.toLowerCase(), line, column));
        } else if (ctx.arrayOperation() != null) {
            // Handle array operations
            String opName = ctx.arrayOperation().getText();
            if (opName.equals("[")) {
                operations.add(new ValueOperation("ARRAY_MARKER", line, column));
            } else if (opName.equals("]")) {
                operations.add(createNamedOperation("]", line, column));
            }
        } else if (ctx.IDENTIFIER() != null) {
            // Check if it's a variable/parameter reference in a function definition
            String name = ctx.IDENTIFIER().getText();
            
            if (currentFunction != null && 
                functionParameters.containsKey(currentFunction) && 
                functionParameters.get(currentFunction).contains(name)) {
                
                // It's a parameter reference in a function body
                // For now, we'll create a special variable operation
                operations.add(new ValueOperation(name, line, column));
                operations.add(createNamedOperation("var", line, column));
            } else {
                // Otherwise it's a function call or global variable reference
                operations.add(new FunctionCallUserOperation(name, line, column));
            }
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
    
    /**
     * Helper method to create a named operation.
     */
    private NamedOperation createNamedOperation(String name, int line, int column) {
        // Get the operation executor from the Parser's operations map
        // We need to create a temporary parser to access its operations map
        Parser tempParser = new Parser(new ArrayList<>());
        java.lang.reflect.Field operationsField;
        try {
            operationsField = Parser.class.getDeclaredField("operations");
            operationsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, NamedOperation.OperationExecutor> operations = 
                (Map<String, NamedOperation.OperationExecutor>) operationsField.get(tempParser);
            
            NamedOperation.OperationExecutor executor = operations.get(name.toLowerCase());
            if (executor != null) {
                return new NamedOperation(name, executor, line, column);
            } else {
                // Add implementations for operations that might not be in the Parser class
                NamedOperation.OperationExecutor customExecutor = null;
                
                switch (name.toLowerCase()) {
                    // Basic stack manipulations
                    case "dup":
                        customExecutor = (stack, env) -> stack.dup();
                        break;
                    case "swap":
                        customExecutor = (stack, env) -> stack.swap();
                        break;
                    case "drop":
                        customExecutor = (stack, env) -> stack.drop();
                        break;
                    case "pick":
                        customExecutor = (stack, env) -> {
                            // "pick" expects an integer n on top of the stack:
                            // "n pick" duplicates the nth item from the top (0-based).
                            // For example, "0 pick" is effectively DUP,
                            // "1 pick" duplicates the second-from-top item, etc.
                            
                            if (stack.size() < 1) {
                                throw new IllegalArgumentException("Stack underflow for pick: need an integer index");
                            }
                            
                            // Pop the index n
                            io.github.stackphy.model.StackItem nItem = stack.pop();
                            
                            // Ensure nItem is numeric
                            if (!(nItem instanceof io.github.stackphy.model.Primitive)) {
                                throw new IllegalArgumentException("pick requires a numeric index on top of the stack");
                            }
                            
                            double nVal = ((io.github.stackphy.model.Primitive) nItem).getDoubleValue();
                            int nInt = (int) nVal;  // e.g. if top was 2.0, nInt=2
                            
                            // Check bounds
                            if (nInt < 0 || nInt >= stack.size()) {
                                throw new IllegalArgumentException("Invalid index for pick: " + nInt);
                            }
                            
                            // Create a temporary list to hold items we need to move
                            List<io.github.stackphy.model.StackItem> tempItems = new ArrayList<>();
                            
                            // Pop nInt items from the stack
                            for (int i = 0; i < nInt; i++) {
                                tempItems.add(stack.pop());
                            }
                            
                            // Now the item we want to pick is at the top of the stack
                            io.github.stackphy.model.StackItem itemToPick = stack.peek();
                            
                            // Create a new copy based on the type of the item
                            io.github.stackphy.model.StackItem copiedItem;
                            if (itemToPick instanceof io.github.stackphy.model.Primitive) {
                                // For primitives, create a new primitive with the same value
                                io.github.stackphy.model.Primitive primItem = (io.github.stackphy.model.Primitive) itemToPick;
                                if (primItem.isNumeric()) {
                                    copiedItem = new io.github.stackphy.model.Primitive(primItem.getDoubleValue());
                                } else if (primItem.isString()) {
                                    copiedItem = new io.github.stackphy.model.Primitive(primItem.getStringValue());
                                } else {
                                    // Fall back to string representation for other types
                                    copiedItem = new io.github.stackphy.model.Primitive(primItem.toString());
                                }
                            } else if (itemToPick instanceof io.github.stackphy.model.UserFunction) {
                                // For user functions, it's typically not a good idea to duplicate them,
                                // but we could use the same reference if needed
                                copiedItem = itemToPick;
                            } else {
                                // For other types, you may need to implement specific copying logic
                                // For now, we'll just use the same reference (not a true copy)
                                copiedItem = itemToPick;
                            }
                            
                            // Push the copied item to the stack
                            stack.push(copiedItem);
                            
                            // Push back all the items we popped in reverse order
                            for (int i = tempItems.size() - 1; i >= 0; i--) {
                                stack.push(tempItems.get(i));
                            }
                        };
                        break;
                    case "rot":
                        customExecutor = (stack, env) -> {
                            // Rotate the top 3 items: [a b c] -> [b c a]
                            System.out.println("ROT: stack before = " + stack);
                            if (stack.size() >= 3) {
                                io.github.stackphy.model.StackItem c = stack.pop();
                                io.github.stackphy.model.StackItem b = stack.pop();
                                io.github.stackphy.model.StackItem a = stack.pop();
                                stack.push(b);
                                stack.push(c);
                                stack.push(a);
                                System.out.println("ROT: stack after = " + stack);
                            } else {
                                throw new IllegalArgumentException("Stack underflow for rot operation");
                            }
                        };
                        break;
                    case "over":
                        customExecutor = (stack, env) -> {
                            // Duplicate the second item: [a b] -> [a b a]
                            if (stack.size() >= 2) {
                                io.github.stackphy.model.StackItem b = stack.pop();
                                io.github.stackphy.model.StackItem a = stack.peek();
                                stack.push(b);
                                stack.push(a);
                            } else {
                                throw new IllegalArgumentException("Stack underflow for over operation");
                            }
                        };
                        break;
                    case "nip":
                        customExecutor = (stack, env) -> {
                            // Remove the second item: [a b] -> [b]
                            if (stack.size() >= 2) {
                                io.github.stackphy.model.StackItem b = stack.pop();
                                stack.drop();
                                stack.push(b);
                            } else {
                                throw new IllegalArgumentException("Stack underflow for nip operation");
                            }
                        };
                        break;
                    case "tuck":
                        customExecutor = (stack, env) -> {
                            // Copy the top item below the second: [a b] -> [b a b]
                            if (stack.size() >= 2) {
                                io.github.stackphy.model.StackItem b = stack.pop();
                                io.github.stackphy.model.StackItem a = stack.pop();
                                stack.push(b);
                                stack.push(a);
                                stack.push(b);
                            } else {
                                throw new IllegalArgumentException("Stack underflow for tuck operation");
                            }
                        };
                        break;
                    
                    // Math operations
                    case "+":
                        customExecutor = (stack, env) -> {
                            // Add the top two items
                            io.github.stackphy.model.StackItem b = stack.pop();
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            if (a instanceof io.github.stackphy.model.Primitive && b instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                io.github.stackphy.model.Primitive pb = (io.github.stackphy.model.Primitive) b;
                                
                                if (pa.isNumeric() && pb.isNumeric()) {
                                    double result = pa.getDoubleValue() + pb.getDoubleValue();
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Addition requires two numeric values");
                        };
                        break;
                    case "-":
                        customExecutor = (stack, env) -> {
                            // Subtract the top item from the second item
                            io.github.stackphy.model.StackItem b = stack.pop();
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            if (a instanceof io.github.stackphy.model.Primitive && b instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                io.github.stackphy.model.Primitive pb = (io.github.stackphy.model.Primitive) b;
                                
                                if (pa.isNumeric() && pb.isNumeric()) {
                                    double result = pa.getDoubleValue() - pb.getDoubleValue();
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Subtraction requires two numeric values");
                        };
                        break;
                    case "*":
                        customExecutor = (stack, env) -> {
                            // Multiply the top two items
                            io.github.stackphy.model.StackItem b = stack.pop();
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            if (a instanceof io.github.stackphy.model.Primitive && b instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                io.github.stackphy.model.Primitive pb = (io.github.stackphy.model.Primitive) b;
                                
                                if (pa.isNumeric() && pb.isNumeric()) {
                                    double result = pa.getDoubleValue() * pb.getDoubleValue();
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Multiplication requires two numeric values");
                        };
                        break;
                    case "/":
                        customExecutor = (stack, env) -> {
                            // Divide the second item by the top item
                            io.github.stackphy.model.StackItem b = stack.pop();
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            System.out.println("DIVISION: a=" + a + ", b=" + b);
                            
                            if (a instanceof io.github.stackphy.model.Primitive && b instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                io.github.stackphy.model.Primitive pb = (io.github.stackphy.model.Primitive) b;
                                
                                System.out.println("DIVISION as doubles: a=" + pa.getDoubleValue() + ", b=" + pb.getDoubleValue());
                                
                                if (pa.isNumeric() && pb.isNumeric()) {
                                    if (pb.getDoubleValue() == 0.0) {
                                        System.out.println("DIVISION BY ZERO DETECTED");
                                        throw new IllegalArgumentException("Division by zero");
                                    }
                                    double result = pa.getDoubleValue() / pb.getDoubleValue();
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Division requires two numeric values");
                        };
                        break;
                    case "negate":
                        customExecutor = (stack, env) -> {
                            // Negate the top item
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            if (a instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                
                                if (pa.isNumeric()) {
                                    double result = -pa.getDoubleValue();
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Negation requires a numeric value");
                        };
                        break;
                    case "sqrt":
                        customExecutor = (stack, env) -> {
                            System.out.println("SQRT: stack before = " + stack);

                            // Square root of the top item
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            if (a instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                
                                if (pa.isNumeric()) {
                                    double value = pa.getDoubleValue();
                                    if (value < 0.0) {
                                        throw new IllegalArgumentException("Square root of negative number");
                                    }
                                    double result = Math.sqrt(value);
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    System.out.println("SQRT: stack after = " + stack);
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Square root requires a numeric value");
                        };
                        break;
                    case "exp":
                        customExecutor = (stack, env) -> {
                            // Exponential of the top item
                            System.out.println("EXP: stack before = " + stack);
                            io.github.stackphy.model.StackItem a = stack.pop();
                            
                            if (a instanceof io.github.stackphy.model.Primitive) {
                                io.github.stackphy.model.Primitive pa = (io.github.stackphy.model.Primitive) a;
                                
                                if (pa.isNumeric()) {
                                    double result = Math.exp(pa.getDoubleValue());
                                    System.out.println("EXP: calculating e^" + pa.getDoubleValue() + " = " + result);
                                    stack.push(new io.github.stackphy.model.Primitive(result));
                                    System.out.println("EXP: stack after = " + stack);
                                    return;
                                }
                            }
                            
                            throw new IllegalArgumentException("Exponential requires a numeric value");
                        };
                        break;
                    case "pi":
                        customExecutor = (stack, env) -> {
                            // Push π onto the stack
                            System.out.println("PI: stack before = " + stack);
                            stack.push(new io.github.stackphy.model.Primitive(Math.PI));
                            System.out.println("PI: stack after = " + stack);
                        };
                        break;
                }
                
                if (customExecutor != null) {
                    return new NamedOperation(name, customExecutor, line, column);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Error accessing operations map: " + e.getMessage());
        }
        
        // Fallback: return a dummy executor that throws an exception
        NamedOperation.OperationExecutor fallbackExecutor = (stack, env) -> {
            throw new UnsupportedOperationException("Operation not implemented: " + name);
        };
        
        return new NamedOperation(name, fallbackExecutor, line, column);
    }
}