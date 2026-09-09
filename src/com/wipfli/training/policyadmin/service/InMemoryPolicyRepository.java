package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

/**
 * Assignment 11: the only place that physically stores policies in memory.
 * These 4 maps used to live directly inside PolicyRegister - they've been moved
 * here unchanged, so PolicyRegister can depend on the PolicyRepository interface
 * instead of owning storage itself (Repository pattern).
 */
@Repository
public class InMemoryPolicyRepository implements PolicyRepository {

    private final Map<String, Policy> policies = new HashMap<>();

    private final Map<String, List<Policy>> policiesByCustomer = new HashMap<>();

    private final Map<VehicleType, List<Policy>> policiesByVehicleType = new EnumMap<>(VehicleType.class);

    private final TreeMap<LocalDate, List<Policy>> policiesByExpiryDate = new TreeMap<>();

    @Override
    public void save(Policy policy) {
        String number = policy.getPolicyNumber();

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

    @Override
    public boolean existsByNumber(String policyNumber) {
        return policies.containsKey(policyNumber);
    }

    @Override
    public Optional<Policy> findByNumber(String policyNumber) {
        return Optional.ofNullable(policies.get(policyNumber));
    }

    @Override
    public List<Policy> findByCustomer(String customerName) {
        List<Policy> list = policiesByCustomer.get(customerName);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<Policy> findByVehicleType(VehicleType vehicleType) {
        List<Policy> list = policiesByVehicleType.get(vehicleType);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<Policy> findExpiringWithin(int days, LocalDate from) {
        LocalDate cutoff = from.plusDays(days);
        List<Policy> result = new ArrayList<>();

        // subMap grabs only the dates between 'from' and 'cutoff' (both inclusive).
        for (List<Policy> policiesOnDate : policiesByExpiryDate.subMap(from, true, cutoff, true).values()) {
            result.addAll(policiesOnDate);
        }
        return result;
    }

    @Override
    public Collection<Policy> findAll() {
        return policies.values();
    }
}
