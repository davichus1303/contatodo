package com.contatodo.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Money} value object.
 */
class MoneyTest {

    @Test
    void ofRoundsToTwoDecimalsHalfUp() {
        assertEquals(10.13, Money.of(10.125).toDouble());
        assertEquals(10.12, Money.of(10.124).toDouble());
    }

    @Test
    void ofRejectsNull() {
        assertThrows(NullPointerException.class, () -> Money.of(null));
    }

    @Test
    void addSumsWithoutFloatingDrift() {
        Money total = Money.of(0.1).add(Money.of(0.2));
        assertEquals(0.30, total.toDouble());
    }

    @Test
    void multiplyScalesByQuantity() {
        assertEquals(24.90, Money.of(8.30).multiply(3).toDouble());
    }

    @Test
    void divideComputesWeightedAverage() {
        assertEquals(3.34, Money.of(10.01).divide(3).toDouble());
    }

    @Test
    void divideRejectsNonPositiveDivisor() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(10.0).divide(0));
    }

    @Test
    void isGreaterThanComparesByValue() {
        assertTrue(Money.of(20.0).isGreaterThan(Money.of(19.99)));
    }

    @Test
    void zeroRepresentsZeroAmount() {
        assertEquals(0.0, Money.zero().toDouble());
    }
}
