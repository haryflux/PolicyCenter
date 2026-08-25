package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * 1. A valid policy passes without throwing
 * 2. Invalid age throws InvalidPolicyDataException
 */
class PolicyValidatorTest {

    @Test
    @DisplayName("A valid policy passes validation without throwing")
    void validPolicy_passesWithoutThrowing() {
        // pol2001 is a valid policy - building & validating it should not throw
        assertDoesNotThrow(() -> {
            PolicyValidator.validate(TestFixtures.pol2001());
        });
    }

    @Test
    @DisplayName("Age below minimum throws InvalidPolicyDataException")
    void ageBelowMinimum_throwsInvalidPolicyDataException() {
        assertThrows(InvalidPolicyDataException.class, () -> {
            new Customer("Ravi", 15);   // Age is under 18
        });
    }
}