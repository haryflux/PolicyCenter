package com.wipfli.training.policyadmin.dto;

/**
 * Assignment 11: the response for "what would this policy's premium be?".
 * Shows BOTH numbers so it's obvious what the no-claim discount actually did -
 * standardPremium is the plain StandardPremiumCalculator result, discountedPremium
 * is what NoClaimBonusCalculator produces after wrapping it (Decorator pattern
 * from Assignment 10, still untouched here).
 */
public class PremiumResponse {

    private String policyNumber;
    private double standardPremium;
    private double discountedPremium;

    public PremiumResponse() {
    }

    public PremiumResponse(String policyNumber, double standardPremium, double discountedPremium) {
        this.policyNumber = policyNumber;
        this.standardPremium = standardPremium;
        this.discountedPremium = discountedPremium;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public double getStandardPremium() {
        return standardPremium;
    }

    public double getDiscountedPremium() {
        return discountedPremium;
    }
}
