package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;

/**
 * Calculates the premium and applies a no-claim discount if the customer is eligible for it.
 */

public class NoClaimBonusCalculator implements PremiumCalculable {

    private static final double NO_CLAIM_DISCOUNT_PERCENT = 0.10;

    private final PremiumCalculable standardCalculator;

    public NoClaimBonusCalculator() {
        this.standardCalculator = new StandardPremiumCalculator();
    }

/**
 * Applies a 10% discount if the customer has no recorded claims.
 */

    @Override
    public double calculatePremium(Policy policy) {
        double premium = standardCalculator.calculatePremium(policy);

        if (hasNoClaims(policy)) {
            premium -= noClaimDiscount(premium);
        }

        return premium;
    }

    private boolean hasNoClaims(Policy policy) {
        return policy.getPreviousClaims() == 0;
    }

    private double noClaimDiscount(double premium) {
        return premium * NO_CLAIM_DISCOUNT_PERCENT;
    }
}