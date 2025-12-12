/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.guinetik.terminaljavadocs;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Example class to demonstrate JXR with Prism.js syntax highlighting.
 *
 * <p>This class showcases various Java language features that benefit
 * from proper syntax highlighting:</p>
 * <ul>
 *   <li>Classes and interfaces</li>
 *   <li>Annotations</li>
 *   <li>Generics</li>
 *   <li>Lambda expressions</li>
 *   <li>String literals and comments</li>
 * </ul>
 *
 * @author guinetik
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class Example {

    /** The default message constant */
    public static final String DEFAULT_MESSAGE = "Hello, Terminal!";

    /** Maximum retry count */
    private static final int MAX_RETRIES = 3;

    /** List of registered listeners */
    private final List<Consumer<String>> listeners = new ArrayList<>();

    /** Current message */
    private String message;

    /** Retry counter */
    private int retryCount = 0;

    /**
     * Creates a new Example with the default message.
     */
    public Example() {
        this(DEFAULT_MESSAGE);
    }

    /**
     * Creates a new Example with a custom message.
     *
     * @param message the message to use
     */
    public Example(String message) {
        this.message = message;
    }

    /**
     * Gets the current message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets a new message and notifies all listeners.
     *
     * @param message the new message
     * @throws IllegalArgumentException if message is null
     */
    public void setMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        this.message = message;
        notifyListeners();
    }

    /**
     * Adds a listener that will be notified when the message changes.
     *
     * @param listener the listener to add
     */
    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    /**
     * Processes the message with various transformations.
     *
     * @return the processed message
     */
    public String processMessage() {
        // Using streams and lambda expressions
        return message.chars()
            .mapToObj(c -> String.valueOf((char) c))
            .map(String::toUpperCase)
            .reduce("", (a, b) -> a + b);
    }

    /**
     * Retries an operation up to MAX_RETRIES times.
     *
     * @param operation the operation to retry
     * @return true if successful, false otherwise
     */
    public boolean retryOperation(Runnable operation) {
        while (retryCount < MAX_RETRIES) {
            try {
                operation.run();
                return true;
            } catch (Exception e) {
                retryCount++;
                // Log the error
                System.err.println("Attempt " + retryCount + " failed: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Notifies all registered listeners about the message change.
     */
    private void notifyListeners() {
        for (Consumer<String> listener : listeners) {
            listener.accept(message);
        }
    }

    /**
     * Main method for testing.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Example example = new Example();

        // Add a listener using lambda
        example.addListener(msg -> System.out.println("Message changed: " + msg));

        // Set a new message
        example.setMessage("Welcome to Terminal Javadocs!");

        // Process and print the result
        String processed = example.processMessage();
        System.out.println("Processed: " + processed);
    }
}
