package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;

/**
 * Assignment 10 (Decorator): a decorator that wraps another premium calculator
 * and applies a no-claim discount on top of whatever premium the wrapped
 * calculator returns. It reuses the wrapped calculator instead of duplicating its math.
 */
public class NoClaimBonusCalculator implements PremiumCalculable {

    private static final double NO_CLAIM_DISCOUNT_PERCENT = 0.10;

    // Assignment 10 (Decorator): the calculator we wrap is passed in, not created here.
    private final PremiumCalculable wrappedCalculator;

    // Assignment 10: constructor now takes the wrapped calculator (dependency injection).
    public NoClaimBonusCalculator(PremiumCalculable wrappedCalculator) {
        this.wrappedCalculator = wrappedCalculator;
    }

    /**
     * Delegates to the wrapped calculator for the base premium,
     * then applies a 10% discount only if the policy has no claims.
     */
    @Override
    public double calculatePremium(Policy policy) {
        // Assignment 10: delegate to the wrapped calculator instead of doing the math ourselves.
        double premium = wrappedCalculator.calculatePremium(policy);

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