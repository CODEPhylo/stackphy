package io.github.stackphy.model;

/**
 * Base interface for all items that can be placed on the stack.
 */
public interface StackItem {
    /**
     * Returns the type of this stack item.
     * @return The stack item type
     */
    StackItemType getType();
}
