package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.exception.DuplicatePolicyNumberException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores policies and provides quick ways to find them
 * by policy number, customer, vehicle type, and expiry date.
 * All 4 structures are updated through add().
 *
 * The premium calculator is injected through the constructor (Strategy pattern) -
 * this class depends on the PremiumCalculable interface, never on a specific calculator.
 */
@Service
public class PolicyRegister {

    private final Map<String, Policy> policies = new HashMap<>();

    private final Map<String, List<Policy>> policiesByCustomer = new HashMap<>();

    // private final Map<VehicleType, List<Policy>> policiesByVehicleType = new HashMap<>();
    private final Map<VehicleType, List<Policy>> policiesByVehicleType = new EnumMap<>(VehicleType.class);

    private final TreeMap<LocalDate, List<Policy>> policiesByExpiryDate = new TreeMap<>();

    // Assignment 10 (Strategy): the calculator is injected through the constructor,
    // such that this class depends on the PremiumCalculable interface, not a specific calculator.
    private final PremiumCalculable premiumCalculator;

    // Assignment 11: two beans (standardPremiumCalculator, noClaimBonusCalculator) implement
    // PremiumCalculable, so Spring can't pick one automatically. @Qualifier tells it exactly
    // which bean to inject here - the discounted (NoClaimBonusCalculator) chain, matching what
    // the composition root always wired in manually in Assignment 10.
    public PolicyRegister(@Qualifier("noClaimBonusCalculator") PremiumCalculable premiumCalculator) {
        this.premiumCalculator = premiumCalculator;
    }

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

    /** Returns all policies for a customer. Empty list if none. */
    public List<Policy> findByCustomer(String customerName) {
        List<Policy> list = policiesByCustomer.get(customerName);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /** Returns all policies for a vehicle type. Empty list if none. */
    public List<Policy> findByVehicleType(VehicleType vehicleType) {
        List<Policy> list = policiesByVehicleType.get(vehicleType);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * Returns all policies expiring within the given number of days from the start date,
     * sorted by expiry date (nearest first). Uses subMap to grab only the dates in range.
     */
    public List<Policy> findExpiringWithin(int days, LocalDate from) {
        LocalDate cutoff = from.plusDays(days);
        List<Policy> result = new ArrayList<>();

        // subMap grabs only the dates between 'from' and 'cutoff' (both inclusive).
        for (List<Policy> policiesOnDate : policiesByExpiryDate.subMap(from, true, cutoff, true).values()) {
            result.addAll(policiesOnDate);
        }
        return result;
    }

    /**
     * Calculates the total premium by each vehicle type.
     * Uses basic for loops to group and add premiums, via the injected calculator.
     */
    public Map<VehicleType, Double> totalPremiumByVehicleType() {
        Map<VehicleType, Double> totals = new HashMap<>();

        for (Map.Entry<VehicleType, List<Policy>> entry : policiesByVehicleType.entrySet()) {
            double total = 0.0;
            for (Policy p : entry.getValue()) {
                // Assignment 10: use the injected calculator instead of a local new StandardPremiumCalculator()
                total += premiumCalculator.calculatePremium(p);
            }
            totals.put(entry.getKey(), total);
        }
        return totals;
    }

    /** Returns all policies, so the menu can list them. */
    public Collection<Policy> getAll() {
        return policies.values();
    }

    //  Assignment 8 - stream based query methods

    /**
     * Assignment 8: Finds a policy by number without throwing.
     * Returns an Optional - empty if no policy matches.
     */
    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        return Optional.ofNullable(policies.get(policyNumber));
    }

    /**
     * Assignment 8: Finds all policies for a customer, using a stream.
     */
    public List<Policy> findByCustomerName(String customerName) {
        return policies.values().stream()
                .filter(p -> p.getCustomer().getName().equals(customerName))
                .collect(Collectors.toList());
    }

    /**
     * Assignment 8: Total premium for one vehicle type, using a stream and the injected calculator.
     */
    public double totalPremiumByVehicleType(VehicleType vehicleType) {
        return policies.values().stream()
                .filter(p -> p.getVehicleType() == vehicleType)
                // Assignment 10: use the injected calculator instead of a local new NoClaimBonusCalculator()
                .mapToDouble(p -> premiumCalculator.calculatePremium(p))
                .sum();
    }

    /**
     * Assignment 8: Counts policies per vehicle type, using groupingBy.
     */
    public Map<VehicleType, Long> countPoliciesByVehicleType() {
        return policies.values().stream()
                .collect(Collectors.groupingBy(
                        Policy::getVehicleType,
                        Collectors.counting()));
    }

    /**
     * Assignment 8: Policies expiring within N days of the reference date,
     * sorted soonest first, using a stream.
     */
    public List<Policy> policiesExpiringWithin(int days, LocalDate referenceDate) {
        LocalDate cutoff = referenceDate.plusDays(days);
        return policies.values().stream()
                .filter(p -> !p.getExpiryDate().isBefore(referenceDate)
                        && !p.getExpiryDate().isAfter(cutoff))
                .sorted(Comparator.comparing(Policy::getExpiryDate))
                .collect(Collectors.toList());
    }

    /**
     * Assignment 8: The customer holding the most policies, using a stream.
     * Returns an Optional because there might be no policies at all.
     */
    public Optional<String> customerWithMostPolicies() {
        return policies.values().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCustomer().getName(),
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * Assignment 8: The 5 highest-premium policies, highest first,
     * using a stream and the injected calculator.
     */
    public List<Policy> top5ByPremium() {
        return policies.values().stream()
                // Assignment 10: use the injected calculator instead of a local new NoClaimBonusCalculator()
                .sorted(Comparator.comparingDouble(
                        (Policy p) -> premiumCalculator.calculatePremium(p)).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
