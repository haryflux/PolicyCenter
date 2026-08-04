package com.wipfli.training.policyadmin.app;

import com.wipfli.training.policyadmin.exception.InvalidPolicyDataException;
import com.wipfli.training.policyadmin.exception.PolicyBusinessException;
import com.wipfli.training.policyadmin.model.BikePolicy;
import com.wipfli.training.policyadmin.model.CarPolicy;
import com.wipfli.training.policyadmin.model.Customer;
import com.wipfli.training.policyadmin.model.Policy;
import com.wipfli.training.policyadmin.model.TruckPolicy;
import com.wipfli.training.policyadmin.model.VehicleType;
import com.wipfli.training.policyadmin.service.PolicyRegister;
import com.wipfli.training.policyadmin.service.PolicyValidator;
import com.wipfli.training.policyadmin.service.StandardPremiumCalculator;

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

    private final PolicyRegister register = new PolicyRegister();
    private final StandardPremiumCalculator standardCalculator = new StandardPremiumCalculator();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new PremiumCalculatorApp().run();
    }

    /**
     * Runs the application until the user chooses to exit.
     * Each menu option is delegated to a dedicated handler method.
     */
    public void run() {
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
                case 6 -> running = false;
                default -> System.out.println("That is not a valid option. Pick 1-6.");
            }
        }
        sc.close();
    }

    private void printMenu() {
        System.out.println("\n===== Policy Register =====");
        System.out.println("1. Add policy");
        System.out.println("2. View policies for a customer");
        System.out.println("3. View policies by vehicle type");
        System.out.println("4. View policies expiring soon");
        System.out.println("5. View premium summary by vehicle type");
        System.out.println("6. Exit");
    }

    // Handlers - each method is responsible for one action done by the user

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
                    policy = new CarPolicy(policyNumber, customer, expiryDate, sc.nextLine().trim(), claims);
                }
                case BIKE -> {
                    int engineCC = readInt("Bike Engine Size (CC): ");
                    policy = new BikePolicy(policyNumber, customer, expiryDate, engineCC, claims);
                }
                case TRUCK -> {
                    double loadTons = readDouble("Truck Load Capacity (tons): ");
                    policy = new TruckPolicy(policyNumber, customer, expiryDate, loadTons, claims);
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
        int days = readInt("Enter number of days: ");
        List<Policy> list = register.findExpiringWithin(days, LocalDate.now());
        if (list.isEmpty()) {
            System.out.println("No policies expiring within " + days + " days.");
            return;
        }
        System.out.println("Policies expiring within " + days + " days (sorted by date):");
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

    // Helper methods used for displaying policy information.

    /**
     * Shows basic details of a policy in one line.
     */

    private void printPolicyLine(Policy p) {
        System.out.println("  " + p.getPolicyNumber()
                + "  " + p.getCustomer().getName()
                + "  " + p.getVehicleType()
                + "  premium " + standardCalculator.calculatePremium(p)
                + "  expires " + p.getExpiryDate());
    }

    /**
     * Shows an error message when an operation cannot be completed.
     */

    private void printRefused(PolicyBusinessException e) {
        System.out.println("REFUSED [" + e.getClass().getSimpleName() + "] "
                + e.getPolicyNumber() + ": " + e.getMessage());
    }

    // Methods used to read input from the user.

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