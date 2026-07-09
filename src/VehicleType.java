public enum VehicleType {
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
