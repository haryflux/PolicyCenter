package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

public class StandardPremiumCalculator implements PremiumCalculable {

    private static final int CLAIM_SURCHARGE = 150;
    private static final double YOUNG_DRIVER_SURCHARGE_PERCENT = 0.20;
    private static final int YOUNG_DRIVER_AGE_LIMIT = 25;

    @Override
    public double calculatePremium(Policy policy) {
        double premium = getBaseRate(policy);
        premium += getYoungDriverSurcharge(policy);
        premium += getClaimSurcharge(policy);
        return premium;
    }

    public int getBaseRate(Policy policy) {
        return findBaseRate(policy.getVehicleType());
    }

    public double getYoungDriverSurcharge(Policy policy) {
        if (!isYoungDriver(policy.getCustomerAge())) {
            return 0;
        }
        int baseRate = getBaseRate(policy);
        return baseRate * YOUNG_DRIVER_SURCHARGE_PERCENT;
    }

    public double getClaimSurcharge(Policy policy) {
        return policy.getPreviousClaims() * CLAIM_SURCHARGE;
    }

    private int findBaseRate(VehicleType vehicleType) {
        return vehicleType.getBaseRate();
    }

    private boolean isYoungDriver(int age) {
        return age < YOUNG_DRIVER_AGE_LIMIT;
    }
}