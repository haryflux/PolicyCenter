package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 1. findByPolicyNumber - a HIT returns the policy (Optional present)
 * 2. findByPolicyNumber - a MISS returns empty Optional
 * 3. countPoliciesByVehicleType - correct counts per type
 */

class PolicyRegisterTest {

    private PolicyRegister register;

    @BeforeEach
    void setUp() {
        register = new PolicyRegister();
        register.add(TestFixtures.pol2001());
        register.add(TestFixtures.pol2002());
        register.add(TestFixtures.pol2003());
        register.add(TestFixtures.pol2004());
        register.add(TestFixtures.pol2005());
        register.add(TestFixtures.pol2006());
    }

    @Test
    @DisplayName("findByPolicyNumber hit -> returns the matching policy")
    void findByPolicyNumber_hit_returnsThePolicy() {
        Optional<Policy> result = register.findByPolicyNumber("POL-2001");
        assertTrue(result.isPresent());
        assertEquals("POL-2001", result.get().getPolicyNumber());
    }

    @Test
    @DisplayName("findByPolicyNumber miss -> returns an empty Optional")
    void findByPolicyNumber_miss_returnsEmptyOptional() {
        Optional<Policy> result = register.findByPolicyNumber("POL-9999");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("countPoliciesByVehicleType -> CAR 3, BIKE 2, TRUCK 1")
    void countPoliciesByVehicleType_returnsCorrectCounts() {
        Map<VehicleType, Long> counts = register.countPoliciesByVehicleType();
        assertEquals(3L,counts.get(VehicleType.CAR));
        assertEquals(2L, counts.get(VehicleType.BIKE));
        assertEquals(1L, counts.get(VehicleType.TRUCK));
    }
}

