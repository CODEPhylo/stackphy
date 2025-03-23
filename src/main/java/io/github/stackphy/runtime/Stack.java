package io.github.stackphy.runtime;

import io.github.stackphy.model.StackItem;
import io.github.stackphy.model.Primitive;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.EmptyStackException;

/**
 * Implementation of the stack for StackPhy language.
 */
public class Stack {
    private final Deque<StackItem> stack = new ArrayDeque<>();
    
    /**
     * Pushes a stack item onto the stack.
     * 
     * @param item The stack item to push
     */
    public void push(StackItem item) {
        stack.push(item);
    }
    
    /**
     * Pushes a raw value onto the stack, automatically wrapping it in a Primitive.
     * 
     * @param value The value to push (Number, String, or Array)
     */
    public void pushValue(Object value) {
        push(new Primitive(value));
    }
    
    /**
     * Pops an item from the stack.
     * 
     * @return The popped item
     * @throws EmptyStackException if the stack is empty
     */
    public StackItem pop() {
        if (stack.isEmpty()) {
            throw new EmptyStackException();
        }
        return stack.pop();
    }
    
    /**
     * Pops an item from the stack and casts it to the specified type.
     * 
     * @param <T> The type to cast to
     * @param type The class of the type to cast to
     * @return The popped item, cast to the specified type
     * @throws EmptyStackException if the stack is empty
     * @throws ClassCastException if the item cannot be cast to the specified type
     */
    public <T extends StackItem> T pop(Class<T> type) {
        StackItem item = pop();
        return type.cast(item);
    }
    
    /**
     * Peeks at the top item on the stack without removing it.
     * 
     * @return The top item
     * @throws EmptyStackException if the stack is empty
     */
    public StackItem peek() {
        if (stack.isEmpty()) {
            throw new EmptyStackException();
        }
        return stack.peek();
    }
    
    /**
     * Peeks at the top item on the stack and casts it to the specified type.
     * 
     * @param <T> The type to cast to
     * @param type The class of the type to cast to
     * @return The top item, cast to the specified type
     * @throws EmptyStackException if the stack is empty
     * @throws ClassCastException if the item cannot be cast to the specified type
     */
    public <T extends StackItem> T peek(Class<T> type) {
        StackItem item = peek();
        return type.cast(item);
    }
    
    /**
     * Returns the number of items on the stack.
     * 
     * @return The stack size
     */
    public int size() {
        return stack.size();
    }
    
    /**
     * Returns whether the stack is empty.
     * 
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    /**
     * Clears the stack.
     */
    public void clear() {
        stack.clear();
    }
    
    /**
     * Duplicates the top item on the stack.
     * 
     * @throws EmptyStackException if the stack is empty
     */
    public void dup() {
        StackItem item = peek();
        push(item);
    }   
    
    /**
     * Swaps the top two items on the stack.
     * 
     * @throws EmptyStackException if the stack has fewer than two items
     */
    public void swap() {
        if (size() < 2) {
            throw new EmptyStackException();
        }
        
        StackItem item1 = pop();
        StackItem item2 = pop();
        push(item1);
        push(item2);
    }
    
    /**
     * Drops the top item from the stack.
     * 
     * @throws EmptyStackException if the stack is empty
     */
    public void drop() {
        pop();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Stack [bottom→top]: ");
        Iterator<StackItem> it = stack.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }
}
