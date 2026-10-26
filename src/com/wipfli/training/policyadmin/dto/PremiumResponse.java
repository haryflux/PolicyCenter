package com.wipfli.training.policyadmin.dto;

/**
 * Represents the premium details returned to the client.
 * Includes the calculated standard premium, discounted premium,
 * and the associated policy number.
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
