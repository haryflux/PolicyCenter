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
 * Holds the business rules for managing policies - things like "no duplicate
 * policy numbers" and "you can't look up a policy that doesn't exist."
 *
 * Assignment 11 change: this class used to own its storage directly (4 maps
 * living right here). That's gone now. Storage has moved out into
 * PolicyRepository / InMemoryPolicyRepository, and this class just asks that
 * repository for whatever it needs. Nothing about WHAT this class decides
 * (the rules) has changed - only WHERE the data physically lives.
 *
 * The premium calculator is still injected through the constructor (Strategy
 * pattern) from Assignment 10 - this class depends on the PremiumCalculable
 * interface, never on a specific calculator.
 */
@Service
public class PolicyRegister {

    // Assignment 11: the 4 maps that used to sit here (policies, policiesByCustomer,
    // policiesByVehicleType, policiesByExpiryDate) are gone. They now live inside
    // InMemoryPolicyRepository. This class no longer needs to know HOW policies are
    // stored - it only needs to know that "something" can store and retrieve them,
    // which is exactly what the PolicyRepository interface promises (Repository pattern).
    private final PolicyRepository policyRepository;

    // Assignment 10 (Strategy): the calculator is injected through the constructor,
    // such that this class depends on the PremiumCalculable interface, not a specific calculator.
    private final PremiumCalculable premiumCalculator;

    // Assignment 11: two beans (standardPremiumCalculator, noClaimBonusCalculator) implement
    // PremiumCalculable, so Spring can't pick one automatically. @Qualifier tells it exactly
    // which bean to inject here - the discounted (NoClaimBonusCalculator) chain, matching what
    // the composition root always wired in manually in Assignment 10.
    //
    // policyRepository doesn't need a @Qualifier - there's only ONE class implementing
    // PolicyRepository (InMemoryPolicyRepository), so Spring has no ambiguity to resolve there.
    public PolicyRegister(PolicyRepository policyRepository,
                          @Qualifier("noClaimBonusCalculator") PremiumCalculable premiumCalculator) {
        this.policyRepository = policyRepository;
        this.premiumCalculator = premiumCalculator;
    }

    /**
     * Adds a new policy, after checking it isn't a duplicate.
     * @throws DuplicatePolicyNumberException if the policy number is already used
     */
    public void add(Policy policy) {
        String number = policy.getPolicyNumber();

        // This business rule ("no duplicate policy numbers") is untouched - same
        // exception, same message. Only the mechanics of "how do I check" changed:
        // it now asks the repository instead of poking a Map directly.
        if (policyRepository.existsByNumber(number)) {
            throw new DuplicatePolicyNumberException(number, "a policy with this number is already registered");
        }

        // All 4 "which map do I file this under" bookkeeping now happens inside
        // InMemoryPolicyRepository.save(). This one line replaces what used to be
        // 4 separate blocks of manual map-filling here.
        policyRepository.save(policy);
    }

    /**
     * Finds a policy using its policy number.
     * @throws PolicyNotFoundException if the policy is not found
     */
    public Policy findByNumber(String policyNumber) throws PolicyNotFoundException {
        // The repository only ever reports "found" or "not found" (via Optional) -
        // it never throws. Deciding to turn a miss into a PolicyNotFoundException is
        // a business decision, so that decision stays here, not in the repository.
        return policyRepository.findByNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException(policyNumber, "no policy with this number"));
    }

    /** Returns all policies for a customer. Empty list if none. */
    public List<Policy> findByCustomer(String customerName) {
        // Straight delegation - no business rule here to preserve, just a lookup.
        return policyRepository.findByCustomer(customerName);
    }

    /** Returns all policies for a vehicle type. Empty list if none. */
    public List<Policy> findByVehicleType(VehicleType vehicleType) {
        return policyRepository.findByVehicleType(vehicleType);
    }

    /**
     * Returns all policies expiring within the given number of days from the start date,
     * sorted by expiry date (nearest first).
     */
    public List<Policy> findExpiringWithin(int days, LocalDate from) {
        // The subMap-walking logic moved into InMemoryPolicyRepository.findExpiringWithin(),
        // since that's a storage-lookup detail, not a business rule.
        return policyRepository.findExpiringWithin(days, from);
    }

    /**
     * Calculates the total premium by each vehicle type.
     * Uses basic for loops to group and add premiums, via the injected calculator.
     */
    public Map<VehicleType, Double> totalPremiumByVehicleType() {
        Map<VehicleType, Double> totals = new HashMap<>();

        // Assignment 11: we no longer loop over our own policiesByVehicleType map -
        // we ask the repository "give me everyone of this vehicle type" instead.
        for (VehicleType type : VehicleType.values()) {
            double total = 0.0;
            for (Policy p : policyRepository.findByVehicleType(type)) {
                // Assignment 10: use the injected calculator instead of a local new StandardPremiumCalculator()
                total += premiumCalculator.calculatePremium(p);
            }
            totals.put(type, total);
        }
        return totals;
    }

    /** Returns all policies, so the menu can list them. */
    public Collection<Policy> getAll() {
        // Used to be policies.values() - now the repository is the one holding
        // the actual collection, so we just ask it for everything.
        return policyRepository.findAll();
    }

    //  Assignment 8 - stream based query methods

    /**
     * Assignment 8: Finds a policy by number without throwing.
     * Returns an Optional - empty if no policy matches.
     */
    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        // The repository already returns an Optional itself, so this method now
        // just hands that straight back - no extra wrapping needed.
        return policyRepository.findByNumber(policyNumber);
    }

    /**
     * Assignment 8: Finds all policies for a customer, using a stream.
     */
    public List<Policy> findByCustomerName(String customerName) {
        // Every stream method below follows the same one-line change: wherever we
        // used to write "policies.values()" (our own map), we now write
        // "policyRepository.findAll()" instead. The filtering/sorting/grouping
        // logic after that is 100% unchanged business logic.
        return policyRepository.findAll().stream()
                .filter(p -> p.getCustomer().getName().equals(customerName))
                .collect(Collectors.toList());
    }

    /**
     * Assignment 8: Total premium for one vehicle type, using a stream and the injected calculator.
     */
    public double totalPremiumByVehicleType(VehicleType vehicleType) {
        return policyRepository.findAll().stream()
                .filter(p -> p.getVehicleType() == vehicleType)
                // Assignment 10: use the injected calculator instead of a local new NoClaimBonusCalculator()
                .mapToDouble(p -> premiumCalculator.calculatePremium(p))
                .sum();
    }

    /**
     * Assignment 8: Counts policies per vehicle type, using groupingBy.
     */
    public Map<VehicleType, Long> countPoliciesByVehicleType() {
        return policyRepository.findAll().stream()
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
        return policyRepository.findAll().stream()
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
        return policyRepository.findAll().stream()
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
        return policyRepository.findAll().stream()
                // Assignment 10: use the injected calculator instead of a local new NoClaimBonusCalculator()
                .sorted(Comparator.comparingDouble(
                        (Policy p) -> premiumCalculator.calculatePremium(p)).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
