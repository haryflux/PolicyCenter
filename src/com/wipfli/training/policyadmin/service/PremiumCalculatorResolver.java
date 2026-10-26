package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Centralizes premium calculator selection based on
 * policy claim history. This keeps calculator selection
 * logic separate from premium calculation logic.
 */

@Component
public class PremiumCalculatorResolver {

    private final PremiumCalculable standardCalculator;
    private final PremiumCalculable noClaimBonusCalculator;

    public PremiumCalculatorResolver(
            @Qualifier("standardPremiumCalculator") PremiumCalculable standardCalculator,
            @Qualifier("noClaimBonusCalculator") PremiumCalculable noClaimBonusCalculator) {
        this.standardCalculator = standardCalculator;
        this.noClaimBonusCalculator = noClaimBonusCalculator;
    }

    /**
     * Returns the appropriate premium calculator based on
     * the policy's claim history.
     *
     * @param policy policy for which the premium calculator is required
     * @return no-claim bonus calculator for policies with no claims,
     *         otherwise the standard premium calculator
     * @throws NullPointerException if the policy is null
     */

    public PremiumCalculable resolve(Policy policy) {
        Objects.requireNonNull(policy, "policy must not be null");

// Previous claims are validated during policy creation,
// so only calculator selection is required here.
        int previousClaims = policy.getPreviousClaims();
        return (previousClaims == 0) ? noClaimBonusCalculator : standardCalculator;
    }
}
