package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.CarPolicy;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies PremiumCalculatorResolver's selection logic in isolation, using
 * real StandardPremiumCalculator/NoClaimBonusCalculator instances (no Spring
 * container needed - same plain-Java construction style as PolicyRegisterTest).
 *
 * NOTE: adjust the CarPolicy constructor arguments below if your project's
 * TestFixtures class already provides equivalent sample policies you'd
 * rather reuse instead of constructing CarPolicy directly here.
 */
class PremiumCalculatorResolverTest {

    private PremiumCalculable standardCalculator;
    private PremiumCalculable noClaimBonusCalculator;
    private PremiumCalculatorResolver resolver;

    @BeforeEach
    void setUp() {
        standardCalculator = new StandardPremiumCalculator();
        noClaimBonusCalculator = new NoClaimBonusCalculator(standardCalculator);
        resolver = new PremiumCalculatorResolver(standardCalculator, noClaimBonusCalculator);
    }

    private Policy policyWithClaims(int claims) {
        return new CarPolicy(
                "POL-TEST-" + claims,
                new Customer("Test Driver", 30),
                LocalDate.now().plusDays(60),
                "REG-TEST",
                claims);
    }

    @Test
    void zeroClaims_selectsNoClaimBonusCalculator() {
        Policy policy = policyWithClaims(0);
        assertSame(noClaimBonusCalculator, resolver.resolve(policy));
    }

    @Test
    void oneClaim_selectsStandardCalculator() {
        Policy policy = policyWithClaims(1);
        assertSame(standardCalculator, resolver.resolve(policy));
    }

    @Test
    void multipleClaims_selectsStandardCalculator() {
        Policy policy = policyWithClaims(5);
        assertSame(standardCalculator, resolver.resolve(policy));
    }

    @Test
    void nullPolicy_isRejectedExplicitly() {
        assertThrows(NullPointerException.class, () -> resolver.resolve(null));
    }

    @Test
    void decorator_delegatesToStandardCalculator_andAppliesBonusExactlyOnce() {
        Policy policy = policyWithClaims(0);

        double basePremium = standardCalculator.calculatePremium(policy);
        double resolvedPremium = resolver.resolve(policy).calculatePremium(policy);

        // Applied exactly once: 90% of base (a single 10% discount), not
        // stacked/doubled, and strictly less than the undiscounted base.
        assertTrue(resolvedPremium < basePremium,
                "discounted premium must be lower than the base premium");
        assertEquals(basePremium * 0.90, resolvedPremium, 0.0001,
                "discount must be applied exactly once (10% off base)");
    }

    @Test
    void nonZeroClaimsPolicy_getsFullPremiumWithNoDiscount() {
        Policy policy = policyWithClaims(2);

        double standardPremium = standardCalculator.calculatePremium(policy);
        double resolvedPremium = resolver.resolve(policy).calculatePremium(policy);

        assertEquals(standardPremium, resolvedPremium, 0.0001,
                "policies with claims must get the undiscounted standard premium");
    }
}
