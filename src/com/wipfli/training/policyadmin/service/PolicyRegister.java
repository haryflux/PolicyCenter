package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.exception.DuplicatePolicyNumberException;
import com.wipfli.training.policyadmin.exception.PolicyNotFoundException;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;
import com.wipfli.training.policyadmin.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PolicyRegister is responsible for managing policies and applying
 * business rules. Provides policy registration, lookup,
 * premium calculations, and reporting operations.
 */
@Service
public class PolicyRegister {

    private final PolicyRepository policyRepository;
    private final PremiumCalculatorResolver premiumCalculatorResolver;

    public PolicyRegister(PolicyRepository policyRepository,
                          PremiumCalculatorResolver premiumCalculatorResolver) {
        this.policyRepository = policyRepository;
        this.premiumCalculatorResolver = premiumCalculatorResolver;
    }

    /**
     * Adds a new policy, after checking it isn't a duplicate.
     * @throws DuplicatePolicyNumberException if the policy number is already used
     */

    public void add(Policy policy) {
        String number = policy.getPolicyNumber();
        if (policyRepository.existsByNumber(number)) {
            throw new DuplicatePolicyNumberException(number, "a policy with this number is already registered");
        }
        policyRepository.save(policy);
    }

    /**
     * Finds a policy using its policy number.
     * @throws PolicyNotFoundException if the policy is not found
     */

    public Policy findByNumber(String policyNumber) throws PolicyNotFoundException {
        return policyRepository.findByNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException(policyNumber, "no policy with this number"));
    }

    public List<Policy> findByCustomer(String customerName) {
        return policyRepository.findByCustomer(customerName);
    }

    public List<Policy> findByVehicleType(VehicleType vehicleType) {
        return policyRepository.findByVehicleType(vehicleType);
    }

    public List<Policy> findExpiringWithin(int days, LocalDate from) {
        return policyRepository.findExpiringWithin(days, from);
    }

    /**
     * Calculates the total premium for each vehicle type
     * using the appropriate premium calculation strategy
     * configured for each policy.
     */

    public Map<VehicleType, Double> totalPremiumByVehicleType() {
        Map<VehicleType, Double> totals = new HashMap<>();
        for (VehicleType type : VehicleType.values()) {
            double total = 0.0;
            for (Policy p : policyRepository.findByVehicleType(type)) {
                total += premiumCalculatorResolver.resolve(p).calculatePremium(p);
            }
            totals.put(type, total);
        }
        return totals;
    }

    public Collection<Policy> getAll() {
        return policyRepository.findAll();
    }

    //  Assignment 8 stream-based query methods

    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        return policyRepository.findByNumber(policyNumber);
    }

    public List<Policy> findByCustomerName(String customerName) {
        return policyRepository.findAll().stream()
                .filter(p -> p.getCustomer().getName().equals(customerName))
                .collect(Collectors.toList());
    }

    public double totalPremiumByVehicleType(VehicleType vehicleType) {
        return policyRepository.findAll().stream()
                .filter(p -> p.getVehicleType() == vehicleType)
                .mapToDouble(p -> premiumCalculatorResolver.resolve(p).calculatePremium(p))
                .sum();
    }

    public Map<VehicleType, Long> countPoliciesByVehicleType() {
        return policyRepository.findAll().stream()
                .collect(Collectors.groupingBy(Policy::getVehicleType, Collectors.counting()));
    }

    public List<Policy> policiesExpiringWithin(int days, LocalDate referenceDate) {
        LocalDate cutoff = referenceDate.plusDays(days);
        return policyRepository.findAll().stream()
                .filter(p -> !p.getExpiryDate().isBefore(referenceDate)
                        && !p.getExpiryDate().isAfter(cutoff))
                .sorted(Comparator.comparing(Policy::getExpiryDate))
                .collect(Collectors.toList());
    }

    public Optional<String> customerWithMostPolicies() {
        return policyRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCustomer().getName(),
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public List<Policy> top5ByPremium() {
        return policyRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(
                        (Policy p) -> premiumCalculatorResolver.resolve(p).calculatePremium(p)).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
