package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;

/**
 * Defines a strategy for calculating insurance premiums.
 */

public interface PremiumCalculable {
    double calculatePremium(Policy policy);
}