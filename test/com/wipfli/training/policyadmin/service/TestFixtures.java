package com.wipfli.training.policyadmin.service;

import com.wipfli.training.policyadmin.model.BikePolicy;
import com.wipfli.training.policyadmin.model.CarPolicy;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.TruckPolicy;

import java.time.LocalDate;

/**
 * Same test data - the six seeded policies from Assignment 8.
 */
public class TestFixtures {

    public static Policy pol2001() {   // CAR, Ravi 22, 1 claim
        return new CarPolicy("POL-2001", new Customer("Ravi Kumar", 22),
                LocalDate.of(2026, 8, 15), "REG-2001", 1);
    }

    public static Policy pol2002() {   // TRUCK, Ravi 22, 0 claims
        return new TruckPolicy("POL-2002", new Customer("Ravi Kumar", 22),
                LocalDate.of(2026, 9, 1), 8.0, 0);
    }

    public static Policy pol2003() {   // BIKE, Meena 34, 2 claims
        return new BikePolicy("POL-2003", new Customer("Meena Iyer", 34),
                LocalDate.of(2026, 8, 20), 150, 2);
    }

    public static Policy pol2004() {   // CAR, Ajay 41, 0 claims
        return new CarPolicy("POL-2004", new Customer("Ajay Verma", 41),
                LocalDate.of(2027, 1, 10), "REG-2004", 0);
    }

    public static Policy pol2005() {   // BIKE, Sneha 29, 0 claims
        return new BikePolicy("POL-2005", new Customer("Sneha Rao", 29),
                LocalDate.of(2026, 8, 25), 200, 0);
    }

    public static Policy pol2006() {   // CAR, Ravi 22, 3 claims
        return new CarPolicy("POL-2006", new Customer("Ravi Kumar", 22),
                LocalDate.of(2026, 12, 1), "REG-2006", 3);
    }
}
