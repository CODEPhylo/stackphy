package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for mathematical operations.
 */
public class MathOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("+", this::addOperation);
        operations.put("-", this::subtractOperation);
        operations.put("*", this::multiplyOperation);
        operations.put("/", this::divideOperation);
        operations.put("negate", this::negateOperation);
        operations.put("sqrt", this::sqrtOperation);
        operations.put("exp", this::expOperation);
        operations.put("pi", this::piOperation);
    }
    
    /**
     * Implements the addition operation
     */
    private void addOperation(Stack stack, Environment env) {
        // Add the top two items
        StackItem b = stack.pop();
        StackItem a = stack.pop();
        
        if (a instanceof Primitive && b instanceof Primitive) {
            Primitive pa = (Primitive) a;
            Primitive pb = (Primitive) b;
            
            if (pa.isNumeric() && pb.isNumeric()) {
                double result = pa.getDoubleValue() + pb.getDoubleValue();
                stack.push(new Primitive(result));
                return;
            }
        }
        
        throw new IllegalArgumentException("Addition requires two numeric values");
    }
    
    /**
     * Implements the subtraction operation
     */
    private void subtractOperation(Stack stack, Environment env) {
        // Subtract the top item from the second item
        StackItem b = stack.pop();
        StackItem a = stack.pop();
        
        if (a instanceof Primitive && b instanceof Primitive) {
            Primitive pa = (Primitive) a;
            Primitive pb = (Primitive) b;
            
            if (pa.isNumeric() && pb.isNumeric()) {
                double result = pa.getDoubleValue() - pb.getDoubleValue();
                stack.push(new Primitive(result));
                return;
            }
        }
        
        throw new IllegalArgumentException("Subtraction requires two numeric values");
    }
    
    /**
     * Implements the multiplication operation
     */
    private void multiplyOperation(Stack stack, Environment env) {
        // Multiply the top two items
        StackItem b = stack.pop();
        StackItem a = stack.pop();
        
        if (a instanceof Primitive && b instanceof Primitive) {
            Primitive pa = (Primitive) a;
            Primitive pb = (Primitive) b;
            
            if (pa.isNumeric() && pb.isNumeric()) {
                double result = pa.getDoubleValue() * pb.getDoubleValue();
                stack.push(new Primitive(result));
                return;
            }
        }
        
        throw new IllegalArgumentException("Multiplication requires two numeric values");
    }
    
    /**
     * Implements the division operation
     */
    private void divideOperation(Stack stack, Environment env) {
        // Divide the second item by the top item
        StackItem b = stack.pop();
        StackItem a = stack.pop();
        
        System.out.println("DIVISION: a=" + a + ", b=" + b);
        
        if (a instanceof Primitive && b instanceof Primitive) {
            Primitive pa = (Primitive) a;
            Primitive pb = (Primitive) b;
            
            System.out.println("DIVISION as doubles: a=" + pa.getDoubleValue() + ", b=" + pb.getDoubleValue());
            
            if (pa.isNumeric() && pb.isNumeric()) {
                if (pb.getDoubleValue() == 0.0) {
                    System.out.println("DIVISION BY ZERO DETECTED");
                    throw new IllegalArgumentException("Division by zero");
                }
                double result = pa.getDoubleValue() / pb.getDoubleValue();
                stack.push(new Primitive(result));
                return;
            }
        }
        
        throw new IllegalArgumentException("Division requires two numeric values");
    }
    
    /**
     * Implements the negation operation
     */
    private void negateOperation(Stack stack, Environment env) {
        // Negate the top item
        StackItem a = stack.pop();
        
        if (a instanceof Primitive) {
            Primitive pa = (Primitive) a;
            
            if (pa.isNumeric()) {
                double result = -pa.getDoubleValue();
                stack.push(new Primitive(result));
                return;
            }
        }
        
        throw new IllegalArgumentException("Negation requires a numeric value");
    }
    
    /**
     * Implements the square root operation
     */
    private void sqrtOperation(Stack stack, Environment env) {
        System.out.println("SQRT: stack before = " + stack);

        // Square root of the top item
        StackItem a = stack.pop();
        
        if (a instanceof Primitive) {
            Primitive pa = (Primitive) a;
            
            if (pa.isNumeric()) {
                double value = pa.getDoubleValue();
                if (value < 0.0) {
                    throw new IllegalArgumentException("Square root of negative number");
                }
                double result = Math.sqrt(value);
                stack.push(new Primitive(result));
                System.out.println("SQRT: stack after = " + stack);
                return;
            }
        }
        
        throw new IllegalArgumentException("Square root requires a numeric value");
    }
    
    /**
     * Implements the exponential operation
     */
    private void expOperation(Stack stack, Environment env) {
        // Exponential of the top item
        System.out.println("EXP: stack before = " + stack);
        StackItem a = stack.pop();
        
        if (a instanceof Primitive) {
            Primitive pa = (Primitive) a;
            
            if (pa.isNumeric()) {
                double result = Math.exp(pa.getDoubleValue());
                System.out.println("EXP: calculating e^" + pa.getDoubleValue() + " = " + result);
                stack.push(new Primitive(result));
                System.out.println("EXP: stack after = " + stack);
                return;
            }
        }
        
        throw new IllegalArgumentException("Exponential requires a numeric value");
    }
    
    /**
     * Implements the pi constant operation
     */
    private void piOperation(Stack stack, Environment env) {
        // Push π onto the stack
        System.out.println("PI: stack before = " + stack);
        stack.push(new Primitive(Math.PI));
        System.out.println("PI: stack after = " + stack);
    }
}