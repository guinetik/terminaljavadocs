/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.guinetik.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class for generating and working with Fibonacci sequences.
 *
 * <p>Demonstrates various approaches to calculating Fibonacci numbers including
 * iterative, recursive, and memoized implementations. This class is designed
 * to showcase code coverage and documentation in Terminal Javadocs.</p>
 *
 * <h2>Fibonacci Definition</h2>
 * <p>The Fibonacci sequence is defined as:</p>
 * <pre class="language-java"><code>
 * F(0) = 0
 * F(1) = 1
 * F(n) = F(n-1) + F(n-2) for n &gt; 1
 * </code></pre>
 *
 * <h2>Usage Example</h2>
 * <pre class="language-java"><code>
 * FibonacciSequence fib = new FibonacciSequence();
 * long number = fib.calculate(10);  // Returns 55
 * List&lt;Long&gt; sequence = fib.generateSequence(5);
 * </code></pre>
 *
 * @author guinetik
 * @version 1.0.0
 * @since 2025
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FibonacciSequence {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    /** The maximum index for iterative calculation to avoid overflow */
    public static final int MAX_SAFE_INDEX = 92;

    /** Cache for memoized calculations */
    private final Map<Integer, Long> cache;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Creates a new FibonacciSequence calculator with an empty cache.
     */
    public FibonacciSequence() {
        this.cache = new HashMap<>();
        // Pre-populate cache with base cases
        cache.put(0, 0L);
        cache.put(1, 1L);
    }

    // =========================================================================
    // PUBLIC METHODS
    // =========================================================================

    /**
     * Calculates the nth Fibonacci number using iterative approach.
     *
     * <p>This is the most efficient approach for most use cases, with O(n)
     * time complexity and O(1) space complexity.</p>
     *
     * @param n the index of the Fibonacci number to calculate (0-based)
     * @return the nth Fibonacci number
     * @throws IllegalArgumentException if n is negative or exceeds MAX_SAFE_INDEX
     */
    public long calculate(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }

        if (n > MAX_SAFE_INDEX) {
            throw new IllegalArgumentException(
                "Index cannot exceed " + MAX_SAFE_INDEX + " to avoid overflow"
            );
        }

        if (n <= 1) {
            return n;
        }

        long prev = 0;
        long current = 1;

        for (int i = 2; i <= n; i++) {
            long next = prev + current;
            prev = current;
            current = next;
        }

        return current;
    }

    /**
     * Calculates the nth Fibonacci number using recursive approach with memoization.
     *
     * <p>This approach uses caching to avoid redundant calculations. The first call
     * will be slower, but subsequent calls benefit from the cache.</p>
     *
     * @param n the index of the Fibonacci number to calculate (0-based)
     * @return the nth Fibonacci number
     * @throws IllegalArgumentException if n is negative
     */
    public long calculateMemoized(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }

        if (cache.containsKey(n)) {
            return cache.get(n);
        }

        long result = calculateMemoized(n - 1) + calculateMemoized(n - 2);
        cache.put(n, result);
        return result;
    }

    /**
     * Generates a Fibonacci sequence up to the specified count.
     *
     * @param count the number of Fibonacci numbers to generate
     * @return a list containing the first 'count' Fibonacci numbers
     * @throws IllegalArgumentException if count is negative
     */
    public List<Long> generateSequence(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        List<Long> sequence = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            sequence.add(calculate(i));
        }

        return sequence;
    }

    /**
     * Finds the first Fibonacci number that is greater than or equal to the threshold.
     *
     * @param threshold the minimum value to find
     * @return the first Fibonacci number >= threshold, or -1 if none found within safe range
     * @throws IllegalArgumentException if threshold is negative
     */
    public long findFirstGreaterThan(long threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }

        for (int i = 0; i <= MAX_SAFE_INDEX; i++) {
            long fib = calculate(i);
            if (fib >= threshold) {
                return fib;
            }
        }

        return -1;
    }

    /**
     * Calculates the sum of the first n Fibonacci numbers.
     *
     * @param n the count of Fibonacci numbers to sum
     * @return the sum of the first n Fibonacci numbers
     * @throws IllegalArgumentException if n is negative
     */
    public long sumSequence(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        if (n == 0) {
            return 0;
        }

        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += calculate(i);
        }

        return sum;
    }

    /**
     * Clears the memoization cache.
     *
     * <p>This resets the cache to only contain the base cases (0 and 1).</p>
     */
    public void clearCache() {
        cache.clear();
        cache.put(0, 0L);
        cache.put(1, 1L);
    }

    /**
     * Gets the current size of the memoization cache.
     *
     * @return the number of cached values
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * Checks if a number is in the Fibonacci sequence.
     *
     * @param number the number to check
     * @return true if the number is a Fibonacci number, false otherwise
     */
    public boolean isFibonacciNumber(long number) {
        if (number < 0) {
            return false;
        }

        for (int i = 0; i <= MAX_SAFE_INDEX; i++) {
            long fib = calculate(i);
            if (fib == number) {
                return true;
            }
            if (fib > number) {
                return false;
            }
        }

        return false;
    }

    // =========================================================================
    // MAIN METHOD
    // =========================================================================

    /**
     * Main entry point demonstrating Fibonacci sequence calculations.
     *
     * @param args command line arguments (first argument is optional index)
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  Fibonacci Sequence Calculator");
        System.out.println("=".repeat(50));
        System.out.println();

        FibonacciSequence fibonacci = new FibonacciSequence();

        // Calculate single value
        int index = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        long result = fibonacci.calculate(index);
        System.out.println("Fibonacci(" + index + ") = " + result);
        System.out.println();

        // Generate sequence
        System.out.println("First 10 Fibonacci numbers:");
        List<Long> sequence = fibonacci.generateSequence(10);
        sequence.forEach(System.out::println);
        System.out.println();

        // Sum sequence
        long sum = fibonacci.sumSequence(10);
        System.out.println("Sum of first 10 Fibonacci numbers: " + sum);
    }
}
