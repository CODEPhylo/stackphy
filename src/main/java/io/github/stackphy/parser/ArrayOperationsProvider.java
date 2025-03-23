package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provider for array operations.
 */
public class ArrayOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("]", this::arrayCloseOperation);
    }
    
    /**
     * Implements the array close operation
     */
    private void arrayCloseOperation(Stack stack, Environment env) {
        List<Object> elements = new ArrayList<>();
        
        while (!stack.isEmpty()) {
            StackItem item = stack.pop();
            
            if (item instanceof Primitive) {
                Primitive primitive = (Primitive) item;
                if (primitive.isString() && "ARRAY_MARKER".equals(primitive.getStringValue())) {
                    break;
                }
                // Store the actual value, not the StackItem
                elements.add(0, primitive.getValue());
            } else {
                // For non-primitives, store the item itself
                elements.add(0, item);
            }
        }
        
        // Create a Primitive that wraps the array
        stack.push(new Primitive(elements.toArray()));
    }
}