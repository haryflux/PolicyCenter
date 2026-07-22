package com.wipfli.training.policyadmin.model;

/**
 * Represents a customer who owns an insurance policy.
 * A Customer is immutable after creation. Once validated and created, the customer's name and age cannot be changed.
 */

public class Customer {

    private static final int MINIMUM_AGE = 18;
    private static final int MAXIMUM_AGE = 100;

    private final String name;
    private final int age;


    /**
     * Create a customer after validating the supplied details.
     *
     * @param name customer's name
     * @param age customer's age
     * @throws IllegalArgumentException if name is blank or age is invalid
     */


    public Customer(String name, int age) {
        validateName(name);
        validateAge(age);
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    /**
     * Returns customer information as a string.
     * Package-private was created on purpose because only classes inside the model package should need this formatting.
     */

    String describeForPolicy() {
        return name + " (age " + age + ")";
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be blank.");
        }
    }

    private static void validateAge(int age) {
        if (age < MINIMUM_AGE || age > MAXIMUM_AGE) {
            throw new IllegalArgumentException("Customer age must be between 18 and 100.");
        }
    }
}