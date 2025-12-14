/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.guinetik.examples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FibonacciSequence class.
 *
 * <p>This test class demonstrates how to test the Fibonacci sequence calculator
 * and is designed to be used with code coverage reporting (JaCoCo) to showcase
 * the Terminal Javadocs coverage report styling.</p>
 *
 * @author guinetik
 * @version 1.0.0
 * @since 2025
 */
public class FibonacciSequenceTest {

    private FibonacciSequence fibonacci;

    /**
     * Sets up the test fixture before each test method.
     */
    @BeforeEach
    void setUp() {
        fibonacci = new FibonacciSequence();
    }

    // =========================================================================
    // BASIC CALCULATION TESTS
    // =========================================================================

    /**
     * Tests calculating Fibonacci number at index 0.
     */
    @Test
    void testCalculateAtZero() {
        assertEquals(0L, fibonacci.calculate(0));
    }

    /**
     * Tests calculating Fibonacci number at index 1.
     */
    @Test
    void testCalculateAtOne() {
        assertEquals(1L, fibonacci.calculate(1));
    }

    /**
     * Tests calculating Fibonacci number at index 5.
     */
    @Test
    void testCalculateAtFive() {
        assertEquals(5L, fibonacci.calculate(5));
    }

    /**
     * Tests calculating Fibonacci number at index 10.
     */
    @Test
    void testCalculateAtTen() {
        assertEquals(55L, fibonacci.calculate(10));
    }

    /**
     * Tests calculating Fibonacci number at a larger index.
     */
    @Test
    void testCalculateAtTwenty() {
        assertEquals(6765L, fibonacci.calculate(20));
    }

    // =========================================================================
    // ERROR HANDLING TESTS
    // =========================================================================

    /**
     * Tests that negative index throws IllegalArgumentException.
     */
    @Test
    void testCalculateWithNegativeIndex() {
        assertThrows(IllegalArgumentException.class, () -> fibonacci.calculate(-1));
    }

    /**
     * Tests that index exceeding MAX_SAFE_INDEX throws IllegalArgumentException.
     */
    @Test
    void testCalculateWithIndexTooLarge() {
        assertThrows(
            IllegalArgumentException.class,
            () -> fibonacci.calculate(FibonacciSequence.MAX_SAFE_INDEX + 1)
        );
    }

    // =========================================================================
    // SEQUENCE GENERATION TESTS
    // =========================================================================

    /**
     * Tests generating an empty sequence.
     */
    @Test
    void testGenerateSequenceEmpty() {
        List<Long> sequence = fibonacci.generateSequence(0);
        assertTrue(sequence.isEmpty());
    }

    /**
     * Tests generating a sequence of 5 elements.
     */
    @Test
    void testGenerateSequenceFive() {
        List<Long> sequence = fibonacci.generateSequence(5);
        assertEquals(5, sequence.size());
        assertEquals(List.of(0L, 1L, 1L, 2L, 3L), sequence);
    }

    /**
     * Tests generating a sequence of 10 elements.
     */
    @Test
    void testGenerateSequenceTen() {
        List<Long> sequence = fibonacci.generateSequence(10);
        assertEquals(10, sequence.size());
        assertEquals(34L, sequence.get(10 - 1)); // Last element should be F(9) = 34
    }

    /**
     * Tests that negative count throws IllegalArgumentException.
     */
    @Test
    void testGenerateSequenceNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> fibonacci.generateSequence(-1));
    }

    // =========================================================================
    // MEMOIZATION TESTS
    // =========================================================================

    /**
     * Tests memoized calculation produces same result as iterative.
     */
    @Test
    void testMemoizedCalculation() {
        long iterative = fibonacci.calculate(15);
        long memoized = fibonacci.calculateMemoized(15);
        assertEquals(iterative, memoized);
    }

    /**
     * Tests that cache grows after memoized calculations.
     */
    @Test
    void testCacheGrowth() {
        int initialSize = fibonacci.getCacheSize();
        fibonacci.calculateMemoized(10);
        int newSize = fibonacci.getCacheSize();
        assertTrue(newSize > initialSize);
    }

    /**
     * Tests clearing the cache.
     */
    @Test
    void testClearCache() {
        fibonacci.calculateMemoized(10);
        assertTrue(fibonacci.getCacheSize() > 2);
        fibonacci.clearCache();
        assertEquals(2, fibonacci.getCacheSize()); // Only base cases remain
    }

    // =========================================================================
    // UTILITY TESTS
    // =========================================================================

    /**
     * Tests finding first Fibonacci number greater than threshold.
     */
    @Test
    void testFindFirstGreaterThan() {
        long result = fibonacci.findFirstGreaterThan(10);
        assertEquals(13L, result);
    }

    /**
     * Tests finding first Fibonacci number when threshold is zero.
     */
    @Test
    void testFindFirstGreaterThanZero() {
        long result = fibonacci.findFirstGreaterThan(0);
        assertEquals(0L, result);
    }

    /**
     * Tests that negative threshold throws exception.
     */
    @Test
    void testFindFirstGreaterThanNegative() {
        assertThrows(IllegalArgumentException.class, () -> fibonacci.findFirstGreaterThan(-1));
    }

    /**
     * Tests summing the first Fibonacci numbers.
     */
    @Test
    void testSumSequence() {
        // Sum of first 5: 0 + 1 + 1 + 2 + 3 = 7
        assertEquals(7L, fibonacci.sumSequence(5));
    }

    /**
     * Tests sum of zero Fibonacci numbers.
     */
    @Test
    void testSumSequenceZero() {
        assertEquals(0L, fibonacci.sumSequence(0));
    }

    /**
     * Tests that negative count for sum throws exception.
     */
    @Test
    void testSumSequenceNegative() {
        assertThrows(IllegalArgumentException.class, () -> fibonacci.sumSequence(-1));
    }

    /**
     * Tests checking if a number is in the Fibonacci sequence.
     */
    @Test
    void testIsFibonacciNumber() {
        assertTrue(fibonacci.isFibonacciNumber(5));
        assertTrue(fibonacci.isFibonacciNumber(8));
        assertFalse(fibonacci.isFibonacciNumber(6));
        assertFalse(fibonacci.isFibonacciNumber(9));
    }

    /**
     * Tests checking if zero is a Fibonacci number.
     */
    @Test
    void testIsFibonacciNumberZero() {
        assertTrue(fibonacci.isFibonacciNumber(0));
    }

    /**
     * Tests that negative number is not a Fibonacci number.
     */
    @Test
    void testIsFibonacciNumberNegative() {
        assertFalse(fibonacci.isFibonacciNumber(-1));
    }
}
