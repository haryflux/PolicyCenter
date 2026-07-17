package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;

public interface PremiumCalculable {
    double calculatePremium(Policy policy);
}