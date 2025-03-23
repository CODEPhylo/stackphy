package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provider for base stack manipulation operations.
 */
public class BaseOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        // Basic stack operations
        operations.put("dup", (stack, env) -> stack.dup());
        operations.put("swap", (stack, env) -> stack.swap());
        operations.put("drop", (stack, env) -> stack.drop());
        operations.put("rot", this::rotOperation);
        operations.put("over", this::overOperation);
        operations.put("pick", this::pickOperation);
        operations.put("nip", this::nipOperation);
        operations.put("tuck", this::tuckOperation);
    }
    
    /**
     * Implements the rot operation: [a b c] -> [b c a]
     */
    private void rotOperation(Stack stack, Environment env) {
        System.out.println("ROT: stack before = " + stack);
        if (stack.size() >= 3) {
            StackItem c = stack.pop();
            StackItem b = stack.pop();
            StackItem a = stack.pop();
            stack.push(b);
            stack.push(c);
            stack.push(a);
            System.out.println("ROT: stack after = " + stack);
        } else {
            throw new IllegalArgumentException("Stack underflow for rot operation");
        }
    }
    
    /**
     * Implements the over operation: [a b] -> [a b a]
     */
    private void overOperation(Stack stack, Environment env) {
        if (stack.size() >= 2) {
            StackItem b = stack.pop();
            StackItem a = stack.peek();
            stack.push(b);
            stack.push(a);
        } else {
            throw new IllegalArgumentException("Stack underflow for over operation");
        }
    }
    
    /**
     * Implements the pick operation
     */
    private void pickOperation(Stack stack, Environment env) {
        if (stack.size() < 1) {
            throw new IllegalArgumentException("Stack underflow for pick: need an integer index");
        }
        
        // Pop the index n
        StackItem nItem = stack.pop();
        
        // Ensure nItem is numeric
        if (!(nItem instanceof Primitive)) {
            throw new IllegalArgumentException("pick requires a numeric index on top of the stack");
        }
        
        double nVal = ((Primitive) nItem).getDoubleValue();
        int nInt = (int) nVal;  // e.g. if top was 2.0, nInt=2
        
        // Check bounds
        if (nInt < 0 || nInt >= stack.size()) {
            throw new IllegalArgumentException("Invalid index for pick: " + nInt);
        }
        
        // Create a temporary list to hold items we need to move
        List<StackItem> tempItems = new ArrayList<>();
        
        // Pop nInt items from the stack
        for (int i = 0; i < nInt; i++) {
            tempItems.add(stack.pop());
        }
        
        // Now the item we want to pick is at the top of the stack
        StackItem itemToPick = stack.peek();
        
        // Create a new copy based on the type of the item
        StackItem copiedItem;
        if (itemToPick instanceof Primitive) {
            // For primitives, create a new primitive with the same value
            Primitive primItem = (Primitive) itemToPick;
            if (primItem.isNumeric()) {
                copiedItem = new Primitive(primItem.getDoubleValue());
            } else if (primItem.isString()) {
                copiedItem = new Primitive(primItem.getStringValue());
            } else {
                // Fall back to string representation for other types
                copiedItem = new Primitive(primItem.toString());
            }
        } else {
            // For non-primitives, use the same reference
            copiedItem = itemToPick;
        }
        
        // Push the copied item to the stack
        stack.push(copiedItem);
        
        // Push back all the items we popped in reverse order
        for (int i = tempItems.size() - 1; i >= 0; i--) {
            stack.push(tempItems.get(i));
        }
    }
    
    /**
     * Implements the nip operation: [a b] -> [b]
     */
    private void nipOperation(Stack stack, Environment env) {
        if (stack.size() >= 2) {
            StackItem b = stack.pop();
            stack.drop();
            stack.push(b);
        } else {
            throw new IllegalArgumentException("Stack underflow for nip operation");
        }
    }
    
    /**
     * Implements the tuck operation: [a b] -> [b a b]
     */
    private void tuckOperation(Stack stack, Environment env) {
        if (stack.size() >= 2) {
            StackItem b = stack.pop();
            StackItem a = stack.pop();
            stack.push(b);
            stack.push(a);
            stack.push(b);
        } else {
            throw new IllegalArgumentException("Stack underflow for tuck operation");
        }
    }
}