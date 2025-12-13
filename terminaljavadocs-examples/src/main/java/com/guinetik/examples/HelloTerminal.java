/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.guinetik.examples;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A comprehensive example class demonstrating Terminal Javadocs JXR styling
 * with Prism.js syntax highlighting.
 *
 * <p>This class showcases various Java language features including:</p>
 * <ul>
 *   <li>Generics and type parameters</li>
 *   <li>Lambda expressions and method references</li>
 *   <li>Stream API operations</li>
 *   <li>Optional handling</li>
 *   <li>Exception handling patterns</li>
 *   <li>Inner classes and enums</li>
 *   <li>Annotations</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * HelloTerminal terminal = new HelloTerminal("World");
 * terminal.displayGreeting();
 * terminal.runAsyncTask("Process data");
 * }</pre>
 *
 * @author guinetik
 * @version 1.0.0
 * @since 2024
 * @see java.util.concurrent.CompletableFuture
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class HelloTerminal {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    /** The default greeting template */
    public static final String DEFAULT_GREETING = "Hello, %s!";

    /** ANSI escape code for green text */
    public static final String ANSI_GREEN = "\u001B[32m";

    /** ANSI escape code to reset text color */
    public static final String ANSI_RESET = "\u001B[0m";

    /** Maximum number of retries for async operations */
    private static final int MAX_RETRIES = 3;

    /** Default timeout in milliseconds */
    private static final long DEFAULT_TIMEOUT_MS = 5000L;

    // =========================================================================
    // INSTANCE FIELDS
    // =========================================================================

    /** The target name for greetings */
    private final String name;

    /** List of registered message handlers */
    private final List<Function<String, String>> handlers;

    /** Current status of the terminal */
    private Status status;

    /** Timestamp of last activity */
    private LocalDateTime lastActivity;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Creates a new HelloTerminal with a default name.
     */
    public HelloTerminal() {
        this("Terminal");
    }

    /**
     * Creates a new HelloTerminal with the specified name.
     *
     * @param name the target name for greetings
     * @throws IllegalArgumentException if name is null or empty
     */
    public HelloTerminal(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        this.handlers = new ArrayList<>();
        this.status = Status.INITIALIZED;
        this.lastActivity = LocalDateTime.now();
    }

    // =========================================================================
    // PUBLIC METHODS
    // =========================================================================

    /**
     * Displays a colorful greeting to the terminal.
     *
     * <p>The greeting is formatted using ANSI escape codes for
     * terminal color support.</p>
     */
    public void displayGreeting() {
        String greeting = String.format(DEFAULT_GREETING, name);
        String coloredOutput = ANSI_GREEN + greeting + ANSI_RESET;

        System.out.println(coloredOutput);
        System.out.println("Timestamp: " + formatTimestamp(LocalDateTime.now()));

        updateStatus(Status.ACTIVE);
    }

    /**
     * Registers a message handler function.
     *
     * @param handler the handler function to register
     * @return this instance for method chaining
     */
    public HelloTerminal addHandler(Function<String, String> handler) {
        handlers.add(handler);
        return this;
    }

    /**
     * Processes a message through all registered handlers.
     *
     * @param message the message to process
     * @return the processed message after all handlers have been applied
     */
    public String processMessage(String message) {
        return handlers.stream()
            .reduce(
                Function.identity(),
                Function::andThen
            )
            .apply(message);
    }

    /**
     * Runs an asynchronous task with the given description.
     *
     * @param taskDescription description of the task to run
     * @return a CompletableFuture containing the task result
     */
    public CompletableFuture<TaskResult> runAsyncTask(String taskDescription) {
        return CompletableFuture.supplyAsync(() -> {
            updateStatus(Status.PROCESSING);

            try {
                // Simulate some work
                Thread.sleep(100);

                String result = String.format(
                    "[%s] Completed: %s",
                    formatTimestamp(LocalDateTime.now()),
                    taskDescription
                );

                updateStatus(Status.ACTIVE);
                return new TaskResult(true, result, null);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                updateStatus(Status.ERROR);
                return new TaskResult(false, null, e);
            }
        });
    }

    /**
     * Finds a value by key with optional default.
     *
     * @param <T> the type of value to find
     * @param key the key to search for
     * @param defaultValue the default value if not found
     * @return an Optional containing the found value or default
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> findValue(String key, T defaultValue) {
        // Demonstrating Optional usage
        return Optional.ofNullable(key)
            .filter(k -> !k.isEmpty())
            .map(k -> {
                // In a real implementation, this would look up the value
                return defaultValue;
            });
    }

    /**
     * Generates a sequence of numbers with transformations.
     *
     * @param count the number of elements to generate
     * @return a list of formatted number strings
     */
    public List<String> generateSequence(int count) {
        return IntStream.rangeClosed(1, count)
            .boxed()
            .map(i -> String.format("Item #%03d", i))
            .peek(item -> System.out.println("Generated: " + item))
            .collect(Collectors.toList());
    }

    /**
     * Gets the current status.
     *
     * @return the current status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Gets the last activity timestamp.
     *
     * @return the last activity time
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    // =========================================================================
    // PRIVATE METHODS
    // =========================================================================

    /**
     * Updates the status and records activity timestamp.
     *
     * @param newStatus the new status to set
     */
    private void updateStatus(Status newStatus) {
        this.status = newStatus;
        this.lastActivity = LocalDateTime.now();
    }

    /**
     * Formats a timestamp for display.
     *
     * @param timestamp the timestamp to format
     * @return formatted timestamp string
     */
    private String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    /**
     * Represents the status of the terminal.
     */
    public enum Status {
        /** Terminal has been initialized but not yet used */
        INITIALIZED("Initialized"),

        /** Terminal is actively processing */
        ACTIVE("Active"),

        /** Terminal is processing a task */
        PROCESSING("Processing"),

        /** Terminal encountered an error */
        ERROR("Error"),

        /** Terminal has been shut down */
        SHUTDOWN("Shutdown");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Gets the display name for this status.
         *
         * @return the human-readable status name
         */
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Represents the result of an asynchronous task.
     */
    public static class TaskResult {

        private final boolean success;
        private final String message;
        private final Exception error;

        /**
         * Creates a new TaskResult.
         *
         * @param success whether the task succeeded
         * @param message the result message
         * @param error any error that occurred
         */
        public TaskResult(boolean success, String message, Exception error) {
            this.success = success;
            this.message = message;
            this.error = error;
        }

        /**
         * Checks if the task was successful.
         *
         * @return true if successful, false otherwise
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Gets the result message.
         *
         * @return the message, or null if failed
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets any error that occurred.
         *
         * @return the error, or null if successful
         */
        public Optional<Exception> getError() {
            return Optional.ofNullable(error);
        }

        @Override
        public String toString() {
            if (success) {
                return "TaskResult{success=true, message='" + message + "'}";
            } else {
                return "TaskResult{success=false, error=" + error + "}";
            }
        }
    }

    // =========================================================================
    // MAIN METHOD
    // =========================================================================

    /**
     * Main entry point for testing the HelloTerminal class.
     *
     * @param args command line arguments (first argument used as name)
     */
    public static void main(String[] args) {
        // Parse command line arguments
        String name = args.length > 0 ? args[0] : "World";

        System.out.println("=".repeat(50));
        System.out.println("  Terminal Javadocs - JXR Example");
        System.out.println("=".repeat(50));
        System.out.println();

        // Create instance and display greeting
        HelloTerminal terminal = new HelloTerminal(name);
        terminal.displayGreeting();

        System.out.println();

        // Add message handlers using lambdas
        terminal
            .addHandler(String::toUpperCase)
            .addHandler(msg -> "[PROCESSED] " + msg)
            .addHandler(msg -> msg + " (" + terminal.getStatus() + ")");

        // Process a message
        String result = terminal.processMessage("Hello from the terminal!");
        System.out.println("Processed message: " + result);

        System.out.println();

        // Generate a sequence
        System.out.println("Generating sequence:");
        List<String> sequence = terminal.generateSequence(5);
        System.out.println("Total items: " + sequence.size());

        System.out.println();

        // Run async task
        System.out.println("Running async task...");
        terminal.runAsyncTask("Example async operation")
            .thenAccept(taskResult -> {
                if (taskResult.isSuccess()) {
                    System.out.println("Task result: " + taskResult.getMessage());
                } else {
                    taskResult.getError().ifPresent(e ->
                        System.err.println("Task failed: " + e.getMessage())
                    );
                }
            })
            .join(); // Wait for completion in this example

        System.out.println();
        System.out.println("Final status: " + terminal.getStatus().getDisplayName());
        System.out.println("Last activity: " + terminal.getLastActivity());
    }
}
