package com.wipfli.training.policyadmin.repository;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Defines the operations for storing and retrieving policies.
 * Provides a contract for accessing policy data
 * without exposing the underlying storage mechanism.
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