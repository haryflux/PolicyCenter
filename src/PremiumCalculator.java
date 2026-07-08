import java.util.Scanner;

public class PremiumCalculator {

    enum VehicleType {
        BIKE(1, 300),
        CAR(2, 500),
        TRUCK(3, 800);

        private final int id;
        private final int baseRate;

        VehicleType(int id, int baseRate) {
            this.id = id;
            this.baseRate = baseRate;
        }

        public int getId() {
            return id;
        }

        public int getBaseRate() {
            return baseRate;
        }

        public static VehicleType fromId(int id) {
            for (VehicleType type : VehicleType.values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid vehicle ID");
        }
    }

    private static final int CRASH_SURCHARGE = 150;
    private static final double YOUNG_DRIVER_SURCHARGE_PERCENT = 0.20;
    private static final int YOUNG_DRIVER_AGE_LIMIT = 25;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Age: ");
        int age = sc.nextInt();

        System.out.print("Enter Vehicle (1 for Bike, 2 for Car, 3 for Truck): ");
        int choice = sc.nextInt();

        System.out.print("Enter Number of Past Crashes: ");
        int crashes = sc.nextInt();

        validateAge(age);
        validateVehicleChoice(choice);
        validateCrashes(crashes);

        double premium = calculatePremium(age, choice, crashes);

        System.out.println("\nInsurance Premium Details");
        System.out.println("Age: " + age);

        VehicleType selectedVehicle = VehicleType.fromId(choice);
        System.out.println("Vehicle: " + selectedVehicle.name());

        System.out.println("Past Crashes: " + crashes);
        System.out.println("Premium: " + premium);

        sc.close();
    }

    public static double calculatePremium(int age, int choice, int crashes) {
        int baseRate = findBaseRate(choice);
        double premium = baseRate;

        if (isYoungDriver(age)) {
            premium = premium + (baseRate * YOUNG_DRIVER_SURCHARGE_PERCENT);
        }

        premium = premium + (crashes * CRASH_SURCHARGE);
        return premium;
    }

    public static int findBaseRate(int choice) {
        VehicleType type = VehicleType.fromId(choice);
        return type.getBaseRate();
    }

    public static boolean isYoungDriver(int age) {
        return age < YOUNG_DRIVER_AGE_LIMIT;
    }


    public static void validateAge(int age) {
        if (age <= 0 || age > 100) {
            throw new IllegalArgumentException("Invalid age. Must be between 1 and 100.");
        }
    }

    public static void validateVehicleChoice(int choice) {
        try {
            VehicleType.fromId(choice);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vehicle choice. Please select 1, 2, or 3.");
        }
    }

    public static void validateCrashes(int crashes) {
        if (crashes < 0) {
            throw new IllegalArgumentException("Crashes cannot be negative.");
        }
    }
}
