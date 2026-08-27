package com.wipfli.training.policyadmin.app;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.exception.PolicyBusinessException;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.VehicleType;
import com.wipfli.training.policyadmin.service.PolicyRegister;
import com.wipfli.training.policyadmin.service.PolicyValidator;
import com.wipfli.training.policyadmin.service.NoClaimBonusCalculator;
import com.wipfli.training.policyadmin.service.StandardPremiumCalculator;
import com.wipfli.training.policyadmin.service.PremiumCalculable;
import com.wipfli.training.policyadmin.service.PolicyFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class for the Policy Administration System.
 *
 * This class deals with user interaction by showing menus,
 * collecting input, and delegating work to the appropriate services
 * and model classes.
 *
 * Business rules such as validation, premium calculation, and searching
 * are handled by dedicated classes to keep this class focused on the
 * flow of the application.
 */

public class PremiumCalculatorApp {

    // Assignment 10: composition root - build the calculator chain once.
    // A NoClaimBonusCalculator (Decorator) wrapping a StandardPremiumCalculator (Strategy),
    // then injected into PolicyRegister.
    private final PremiumCalculable standardCalculator = new StandardPremiumCalculator();
    private final PremiumCalculable discountedCalculator = new NoClaimBonusCalculator(standardCalculator);
    private final PolicyRegister register = new PolicyRegister(discountedCalculator);

    private final Scanner sc = new Scanner(System.in);
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2026, 8, 9);

    public static void main(String[] args) {
        new PremiumCalculatorApp().run();
    }

    /**
     * Runs the application until the user chooses to exit.
     * Each menu option is delegated to a dedicated handler method.
     */
    public void run() {
        seedData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> handleAddPolicy();
                case 2 -> handleViewByCustomer();
                case 3 -> handleViewByVehicleType();
                case 4 -> handleViewExpiringSoon();
                case 5 -> handleViewSummary();
                // Assignment 8 - stream based queries
                case 6 -> handleFindByNumber();
                case 7 -> handleFindByCustomerStream();
                case 8 -> handleTotalPremiumForType();
                case 9 -> handleCountByType();
                case 10 -> handleExpiringSoonStream();
                case 11 -> handleCustomerWithMostPolicies();
                case 12 -> handleTop5ByPremium();
                case 13 -> running = false;
                default -> System.out.println("That is not a valid option. Pick 1-13.");
            }
        }
        sc.close();
    }

    /**
     * Loads the fixed Assignment 8 sample data so every fresher's output
     * can be checked against the same worked example.
     */

    private void seedData() {
        // Assignment 10: build policies through PolicyFactory instead of new XPolicy(...)
        register.add(PolicyFactory.createCarPolicy(
                "POL-2001",
                new Customer("Ravi Kumar", 22),
                LocalDate.of(2026, 8, 15),
                "REG-2001", 1));

        register.add(PolicyFactory.createTruckPolicy(
                "POL-2002",
                new Customer("Ravi Kumar", 22),
                LocalDate.of(2026, 9, 1),
                8.0, 0));

        register.add(PolicyFactory.createBikePolicy(
                "POL-2003",
                new Customer("Meena Iyer", 34),
                LocalDate.of(2026, 8, 20),
                150, 2));

        register.add(PolicyFactory.createCarPolicy(
                "POL-2004",
                new Customer("Ajay Verma", 41),
                LocalDate.of(2027, 1, 10),
                "REG-2004", 0));

        register.add(PolicyFactory.createBikePolicy(
                "POL-2005",
                new Customer("Sneha Rao", 29),
                LocalDate.of(2026, 8, 25),
                200, 0));

        register.add(PolicyFactory.createCarPolicy(
                "POL-2006",
                new Customer("Ravi Kumar", 22),
                LocalDate.of(2026, 12, 1),
                "REG-2006", 3));
    }

    private void printMenu() {
        System.out.println("\n===== Policy Register =====");
        System.out.println("1. Add policy");
        System.out.println("2. View policies for a customer");
        System.out.println("3. View policies by vehicle type");
        System.out.println("4. View policies expiring soon");
        System.out.println("5. View premium summary by vehicle type");
        System.out.println("--- Assignment 8 (streams) ---");
        System.out.println("6. Find a policy by number");
        System.out.println("7. Find policies for a customer (stream)");
        System.out.println("8. Total premium for a vehicle type");
        System.out.println("9. Count policies per vehicle type");
        System.out.println("10. Policies expiring within N days of reference date");
        System.out.println("11. Customer with the most policies");
        System.out.println("12. Top 5 policies by premium");
        System.out.println("13. Exit");
    }

    //  Assignment 7 handlers

    /**
     * Collects policy information from the user, creates the appropriate
     * policy type, validates it, and adds it to the register.
     */

    private void handleAddPolicy() {
        try {
            System.out.print("Policy Number: ");
            String policyNumber = sc.nextLine().trim();

            Customer customer = readCustomer();
            VehicleType vehicleType = readVehicleType();
            LocalDate expiryDate = readExpiryDate();
            int claims = readInt("Number of Previous Claims: ");

            Policy policy;
            switch (vehicleType) {
                case CAR -> {
                    System.out.print("Car Registration Number (e.g. KA-05-1234): ");
                    // Assignment 10: build via PolicyFactory, not new CarPolicy(...)
                    policy = PolicyFactory.createCarPolicy(policyNumber, customer, expiryDate, sc.nextLine().trim(), claims);
                }
                case BIKE -> {
                    int engineCC = readInt("Bike Engine Size (CC): ");
                    // Assignment 10: build via PolicyFactory, not new BikePolicy(...)
                    policy = PolicyFactory.createBikePolicy(policyNumber, customer, expiryDate, engineCC, claims);
                }
                case TRUCK -> {
                    double loadTons = readDouble("Truck Load Capacity (tons): ");
                    // Assignment 10: build via PolicyFactory, not new TruckPolicy(...)
                    policy = PolicyFactory.createTruckPolicy(policyNumber, customer, expiryDate, loadTons, claims);
                }
                default -> throw new InvalidPolicyDataException(policyNumber, "Unknown vehicle type.");
            }

            PolicyValidator.validate(policy);
            register.add(policy);
            System.out.println("Created " + policy.getPolicyDetails());
        } catch (PolicyBusinessException e) {
            printRefused(e);
        }
    }

    /**
     * Finds and displays all policies belonging to a customer.
     */

    private void handleViewByCustomer() {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();
        List<Policy> list = register.findByCustomer(name);
        if (list.isEmpty()) {
            System.out.println("No policies found for " + name);
            return;
        }
        System.out.println(name + " has " + list.size() + " policy(ies):");
        for (Policy p : list) {
            printPolicyLine(p);
        }
    }

    /**
     * Displays policies that match a selected vehicle type.
     */

    private void handleViewByVehicleType() {
        VehicleType type = readVehicleType();
        List<Policy> list = register.findByVehicleType(type);
        if (list.isEmpty()) {
            System.out.println("No " + type + " policies found.");
            return;
        }
        System.out.println(type + " policies (" + list.size() + "):");
        for (Policy p : list) {
            printPolicyLine(p);
        }
    }

    /**
     * Displays policies whose expiry date falls within
     * the specified number of days.
     */

    private void handleViewExpiringSoon() {
        System.out.print("From date (YYYY-MM-DD): ");
        LocalDate from = readExpiryDate();
        int days = readInt("Enter number of days: ");
        List<Policy> list = register.findExpiringWithin(days, from);
        if (list.isEmpty()) {
            System.out.println("No policies expiring within " + days + " days from " + from);
            return;
        }
        System.out.println("Policies expiring within " + days + " days from " + from + " (sorted by date):");
        for (Policy p : list) {
            printPolicyLine(p);
        }
    }

    /**
     * Displays the total premium amount grouped by vehicle type.
     */

    private void handleViewSummary() {
        Map<VehicleType, Double> totals = register.totalPremiumByVehicleType();
        if (totals.isEmpty()) {
            System.out.println("No policies yet.");
            return;
        }
        System.out.println("Premium summary by vehicle type:");
        for (Map.Entry<VehicleType, Double> entry : totals.entrySet()) {
            System.out.println("  " + entry.getKey() + " : Rs. " + entry.getValue());
        }
    }

    //  Assignment 8 handlers - each uses a stream method, catches its own errors

    /** Find a policy by number - uses Optional, (no crash if missing). */
    private void handleFindByNumber() {
        System.out.print("Enter policy number: ");
        String number = sc.nextLine().trim();
        String result = register.findByPolicyNumber(number)
                .map(p -> p.getPolicyDetails())
                .orElse("No policy found with number " + number);
        System.out.println(result);
    }

    /** Find all policies for a customer, using the stream method. */
    private void handleFindByCustomerStream() {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();
        List<Policy> list = register.findByCustomerName(name);
        if (list.isEmpty()) {
            System.out.println("No policies found for " + name);
            return;
        }
        for (Policy p : list) {
            printPolicyLine(p);
        }
    }

    /** Total premium for one vehicle type. */
    private void handleTotalPremiumForType() {
        VehicleType type = readVehicleType();
        double total = register.totalPremiumByVehicleType(type);
        System.out.println(type + " total premium: " + total);
    }

    /** Count of policies per vehicle type. */
    private void handleCountByType() {
        Map<VehicleType, Long> counts = register.countPoliciesByVehicleType();
        System.out.println("Policy count by vehicle type:");
        for (Map.Entry<VehicleType, Long> entry : counts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    /** Policies expiring within N days of the fixed reference date, sorted soonest first. */
    private void handleExpiringSoonStream() {
        int days = readInt("Enter number of days: ");
        List<Policy> list = register.policiesExpiringWithin(days, REFERENCE_DATE);
        if (list.isEmpty()) {
            System.out.println("No policies expiring within " + days + " days of " + REFERENCE_DATE);
            return;
        }
        System.out.println("Policies expiring within " + days + " days of " + REFERENCE_DATE + " (soonest first):");
        for (Policy p : list) {
            System.out.println(p.getPolicyNumber()
                    + " | " + p.getCustomer().getName()
                    + " | " + p.getVehicleType()
                    + " | expires " + p.getExpiryDate());
        }
    }

    /** Customer holding the most policies - uses Optional. */
    private void handleCustomerWithMostPolicies() {
        String result = register.customerWithMostPolicies()
                .map(name -> name + " holds the most policies")
                .orElse("No policies yet");
        System.out.println(result);
    }

    /** Top 5 policies by premium, highest first. */
    private void handleTop5ByPremium() {
        List<Policy> list = register.top5ByPremium();
        if (list.isEmpty()) {
            System.out.println("No policies yet.");
            return;
        }
        System.out.println("Top 5 policies by premium (highest first):");
        for (Policy p : list) {
            System.out.println(p.getPolicyNumber()
                    + " | " + p.getCustomer().getName()
                    + " | " + p.getVehicleType()
                    + " -> " + discountedCalculator.calculatePremium(p));
        }
    }

    //  Display helpers (These are Helper methods which are used for displaying policy information.)

    /**
     * Shows basic details of a policy in one line.
     */

    private void printPolicyLine(Policy p) {
        System.out.println("  " + p.getPolicyNumber()
                + "  " + p.getCustomer().getName()
                + "  " + p.getVehicleType()
                + "  premium " + discountedCalculator.calculatePremium(p)
                + "  expires " + p.getExpiryDate());
    }

    /**
     * Shows an error message when an operation cannot be completed.
     */

    private void printRefused(PolicyBusinessException e) {
        System.out.println("REFUSED [" + e.getClass().getSimpleName() + "] "
                + e.getPolicyNumber() + ": " + e.getMessage());
    }

    //  Input reading helpers (These methods are used to read input from the user.)
    /**
     * Collects customer information from the user.
     */

    private Customer readCustomer() {
        System.out.print("Customer Name: ");
        String name = sc.nextLine().trim();
        int age = readInt("Customer Age: ");
        return new Customer(name, age);
    }

    /**
     * Continues prompting until a valid vehicle type is entered.
     */

    private VehicleType readVehicleType() {
        while (true) {
            System.out.print("Vehicle Type (BIKE/CAR/TRUCK): ");
            String input = sc.nextLine().trim().toUpperCase();
            try {
                return VehicleType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Must be BIKE, CAR, or TRUCK.");
            }
        }
    }

    /**
     * Reads and parses an expiry date in YYYY-MM-DD format.
     */

    private LocalDate readExpiryDate() {
        while (true) {
            System.out.print("Expiry Date (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Please use the format YYYY-MM-DD, e.g. 2026-09-30.");
            }
        }
    }

    /**
     * Reads an integer value and keeps prompting until valid input is provided.
     */

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a decimal value and keeps prompting until valid input is provided.
     */

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}