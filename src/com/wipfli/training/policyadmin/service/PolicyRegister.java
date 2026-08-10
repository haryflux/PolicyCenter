package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.exception.DuplicatePolicyNumberException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.EnumMap;

/**
 * Stores policies and provides quick ways to find them
 * by policy number, customer, vehicle type, and expiry date.
 * All 4 structures are updated through add().
 */

public class PolicyRegister {


    private final Map<String, Policy> policies = new HashMap<>();

    private final Map<String, List<Policy>> policiesByCustomer = new HashMap<>();

//    private final Map<VehicleType, List<Policy>> policiesByVehicleType = new HashMap<>();
     private final Map<VehicleType, List<Policy>> policiesByVehicleType = new EnumMap<>(VehicleType.class);

    private final TreeMap<LocalDate, List<Policy>> policiesByExpiryDate = new TreeMap<>();

    /**
     * Adds a new policy and stores it in all required collections.
     * @throws DuplicatePolicyNumberException if the policy number is already used
     */

    public void add(Policy policy) {
        String number = policy.getPolicyNumber();
        if (policies.containsKey(number)) {
            throw new DuplicatePolicyNumberException(number, "a policy with this number is already registered");
        }

        // 1. by number
        policies.put(number, policy);

        // 2. by customer
        String customerName = policy.getCustomer().getName();
        if (!policiesByCustomer.containsKey(customerName)) {
            policiesByCustomer.put(customerName, new ArrayList<>());
        }
        policiesByCustomer.get(customerName).add(policy);

        // 3. by vehicle type
        VehicleType type = policy.getVehicleType();
        if (!policiesByVehicleType.containsKey(type)) {
            policiesByVehicleType.put(type, new ArrayList<>());
        }
        policiesByVehicleType.get(type).add(policy);

        // 4. by expiry date
        LocalDate expiry = policy.getExpiryDate();
        if (!policiesByExpiryDate.containsKey(expiry)) {
            policiesByExpiryDate.put(expiry, new ArrayList<>());
        }
        policiesByExpiryDate.get(expiry).add(policy);
    }

    /**
     * Finds a policy using its policy number.
     * @throws PolicyNotFoundException if the policy is not found
     */

    public Policy findByNumber(String policyNumber) throws PolicyNotFoundException {
        Policy policy = policies.get(policyNumber);
        if (policy == null) {
            throw new PolicyNotFoundException(policyNumber, "no policy with this number");
        }
        return policy;
    }

    /**
     * Returns all policies for a customer.
     * Returns an empty list if none are found.
     */

    public List<Policy> findByCustomer(String customerName) {
        List<Policy> list = policiesByCustomer.get(customerName);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * Returns all policies for a vehicle type.
     * Returns an empty list if none are found.
     */

    public List<Policy> findByVehicleType(VehicleType vehicleType) {
        List<Policy> list = policiesByVehicleType.get(vehicleType);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * Returns all policies that will expire within the given number of days from the start date.
     * The list is sorted by expiry date. (nearest first)
     * Returns an empty list if none are found.
     * Using subMap so the TreeMap gives us only the dates in range directly.
     */

    public List<Policy> findExpiringWithin(int days, LocalDate from) {
        LocalDate cutoff = from.plusDays(days);
        List<Policy> result = new ArrayList<>();

    // subMap grabs only the dates between 'from' and 'cutoff'.
    // 'true, true' means both ends are included.
        for (List<Policy> policiesOnDate : policiesByExpiryDate.subMap
                (from, true, cutoff, true).values()) {
            result.addAll(policiesOnDate);
        }
        return result;
    }

    /**
     * Calculates the total premium by each vehicle type.
     * Uses basic for loops to group and add premiums.
     */

    public Map<VehicleType, Double> totalPremiumByVehicleType() {
        StandardPremiumCalculator calculator = new StandardPremiumCalculator();
        Map<VehicleType, Double> totals = new HashMap<>();

        for (Map.Entry<VehicleType, List<Policy>> entry : policiesByVehicleType.entrySet()) {
            double total = 0.0;
            for (Policy p : entry.getValue()) {
                total += calculator.calculatePremium(p);
            }
            totals.put(entry.getKey(), total);
        }
        return totals;
    }

    /** Returns all policies, so the menu can list them. */
    public Collection<Policy> getAll() {
        return policies.values();
    }
}