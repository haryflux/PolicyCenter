package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Assignment 11: defines storage operations for policies, without specifying
 * how or where they're stored. PolicyRegister depends on this interface,
 * not on a specific storage implementation (Repository pattern).
 */
public interface PolicyRepository {

    void save(Policy policy);

    boolean existsByNumber(String policyNumber);

    Optional<Policy> findByNumber(String policyNumber);

    List<Policy> findByCustomer(String customerName);

    List<Policy> findByVehicleType(VehicleType vehicleType);

    List<Policy> findExpiringWithin(int days, LocalDate from);

    Collection<Policy> findAll();
}