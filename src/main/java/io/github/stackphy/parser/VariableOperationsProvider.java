package io.github.stackphy.parser;

import io.github.stackphy.model.Primitive;
import io.github.stackphy.model.StackItem;
import io.github.stackphy.model.StackItemType;
import io.github.stackphy.model.Variable;
import io.github.stackphy.runtime.Environment;
import io.github.stackphy.runtime.Stack;

import java.util.Map;

/**
 * Provider for variable operations.
 */
public class VariableOperationsProvider implements OperationProvider {
    
    @Override
    public void registerOperations(Map<String, NamedOperation.OperationExecutor> operations) {
        operations.put("~", this::stochasticVariableOperation);
        operations.put("=", this::deterministicVariableOperation);
        operations.put("var", this::varOperation);
        operations.put("observe", this::observeOperation);
    }
    
    /**
     * Implements the stochastic variable definition operation
     */
    private void stochasticVariableOperation(Stack stack, Environment env) {
        // Stack is [Distribution, Name]
        String name = null;
        
        // Pop the variable name (top of stack)
        StackItem nameItem = stack.pop();
        if (nameItem instanceof Primitive) {
            Primitive primitive = (Primitive) nameItem;
            if (primitive.isString()) {
                name = primitive.getStringValue();
            }
        }
        
        if (name == null) {
            throw new IllegalArgumentException("Expected string for variable name");
        }
        
        // Pop the distribution (now top of stack)
        StackItem value = stack.pop();
        
        if (value.getType() != StackItemType.DISTRIBUTION) {
            throw new IllegalArgumentException("Expected distribution for stochastic variable");
        }
        
        env.defineVariable(name, value, true);
    }
    
    /**
     * Implements the deterministic variable definition operation
     */
    private void deterministicVariableOperation(Stack stack, Environment env) {
        // First pop the name (on top of stack)
        String name = null;
        StackItem nameItem = stack.pop();
        
        if (nameItem instanceof Primitive) {
            Primitive primitive = (Primitive) nameItem;
            if (primitive.isString()) {
                name = primitive.getStringValue();
            }
        }
        
        if (name == null) {
            throw new IllegalArgumentException("Expected string for variable name");
        }
        
        // Then pop the value
        StackItem value = stack.pop();
        
        env.defineVariable(name, value, false);
    }
    
    /**
     * Implements the variable reference operation
     */
    private void varOperation(Stack stack, Environment env) {
        String name = null;
        
        StackItem nameItem = stack.pop();
        if (nameItem instanceof Primitive) {
            Primitive primitive = (Primitive) nameItem;
            if (primitive.isString()) {
                name = primitive.getStringValue();
            }
        }
        
        if (name == null) {
            throw new IllegalArgumentException("Expected string for variable name");
        }
        
        Variable variable = env.getVariable(name);
        stack.push(variable);
    }
    
    /**
     * Implements the observe operation
     */
    private void observeOperation(Stack stack, Environment env) {
        String name = null;
        
        StackItem nameItem = stack.pop();
        if (nameItem instanceof Primitive) {
            Primitive primitive = (Primitive) nameItem;
            if (primitive.isString()) {
                name = primitive.getStringValue();
            }
        }
        
        if (name == null) {
            throw new IllegalArgumentException("Expected string for variable name");
        }
        
        StackItem data = stack.pop();
        Variable variable = env.getVariable(name);
        
        if (!variable.isStochastic()) {
            throw new IllegalArgumentException("Cannot observe deterministic variable");
        }
        
        variable.setObservedData(data);
    }
}