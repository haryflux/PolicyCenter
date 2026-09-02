package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * 1. CAR, age 22, 1 claim - 750.0                    (500 + 150 + 100) [ Base Rate + Claim surcharge (1 -> 150) + young driver (0.2 * 500)]
 * 2. TRUCK, age 22, 0 claims - 960.0                 (800 + 160)       [ Base Rate + young driver (0.2 * 800)]
 * 3. BIKE, age 34, 2 claims - 600.0                  (300 + 300)       [ Base Rate + Claim surcharge (2 -> 300)]
 * 4. Young driver surcharge applied when age < 25    (0.2 * Base rate)
 * 5. Claim surcharge = claims * 150
 */
class StandardPremiumCalculatorTest {

    private StandardPremiumCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StandardPremiumCalculator();
    }

    @Test
    @DisplayName("CAR policy, young driver, 1 claim -> premium is 750")
    void carPolicy_youngDriverOneClaim_returns750() {
        Policy policy = TestFixtures.pol2001();
        double premium = calculator.calculatePremium(policy);
        assertEquals(750.0, premium, 0.01);
    }

    @Test
    @DisplayName("TRUCK policy, young driver, 0 claims -> premium is 960")
    void truckPolicy_youngDriverNoClaims_returns960() {
        Policy policy = TestFixtures.pol2002();
        double premium = calculator.calculatePremium(policy);
        assertEquals(960.0, premium, 0.01);
    }

    @Test
    @DisplayName("BIKE policy, older driver, 2 claims -> premium is 600")
    void bikePolicy_olderDriverTwoClaims_returns600() {
        Policy policy = TestFixtures.pol2003();
        double premium = calculator.calculatePremium(policy);
        assertEquals(600.0, premium, 0.01);
    }

    @Test
    @DisplayName("Young driver (age < 25) surcharge is 20% of base rate")
    void youngDriver_surchargeIsTwentyPercentOfBase() {
        Policy policy = TestFixtures.pol2001();   // CAR, age 22, base 500
        double surcharge = calculator.getYoungDriverSurcharge(policy);
        assertEquals(100.0, surcharge, 0.01);     // 20% of 500
    }

    @Test
    @DisplayName("Claim surcharge equals number of claims times 150")
    void claimSurcharge_isClaimsTimes150() {
        Policy policy = TestFixtures.pol2006();    // 3 claims
        double claimSurcharge = calculator.getClaimSurcharge(policy);
        assertEquals(450.0, claimSurcharge, 0.01); // 3 * 150
    }
}