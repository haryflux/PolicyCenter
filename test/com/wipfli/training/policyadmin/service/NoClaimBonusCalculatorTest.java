package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * 1. Zero-claim policy gets 10% discount off the standard premium          (e.g.Pol 2002 standard premium = 960, Therefore 960 - 10% = 864)
 * 2. Policy with claims gets NO discount (same as standard premium)        (e.g. Pol 2001 standard premium = 750, Therefore 750 - 0% = 750)
 */
class NoClaimBonusCalculatorTest {

    private StandardPremiumCalculator standard;
    private NoClaimBonusCalculator noClaim;

    @BeforeEach
    void setUp() {
        standard = new StandardPremiumCalculator();
        noClaim = new NoClaimBonusCalculator();
    }

    @Test
    @DisplayName("If Zero claims then 10% no-claim discount applied off the standard premium")
    void zeroClaims_gets10PercentDiscountOffStandard() {
        Policy policy = TestFixtures.pol2002();                                // TRUCK, 0 claims
        double standardPremium = standard.calculatePremium(policy);            // 960
        double discounted = noClaim.calculatePremium(policy);                  // 960 - 10%
        assertEquals(standardPremium * 0.9, discounted, 0.01);   // 864
    }

    @Test
    @DisplayName("If Policy has claims then no discount, same as standard premium")
    void withClaims_noDiscountApplied() {
        Policy policy = TestFixtures.pol2001();                                // CAR, 1 claim
        double standardPremium = standard.calculatePremium(policy);            // 750
        double result = noClaim.calculatePremium(policy);                      // still 750
        assertEquals(standardPremium, result, 0.01);
    }
}