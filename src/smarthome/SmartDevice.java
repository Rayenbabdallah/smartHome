package smarthome;

/**
 * Base class for all smart devices in the simulator.
 * Tracks power usage, unique ID generation, and the room association.
 */
public abstract class SmartDevice {
    /** Unique identifier for the device (e.g., Light1, Thermostat2). */
    protected final String id;
    /** On/off state of the device. */
    protected boolean isOn = false;
    /** Last timestamp when the device was turned on (ms). */
    protected long lastOnTime = 0;
    /** Accumulated energy usage in kWh. */
    protected double energyUsed = 0.0;
    /** Power consumption rate (kW) when the device is on. */
    protected final double powerUsage;
    /** Reference to the room this device belongs to. */
    protected Room room;

    private static final java.util.HashMap<String, Integer> TYPE_COUNT = new java.util.HashMap<>();

    protected SmartDevice(double powerUsage) {
        this.powerUsage = powerUsage;
        String type = getClass().getSimpleName();
        int count = TYPE_COUNT.getOrDefault(type, 0) + 1;
        TYPE_COUNT.put(type, count);
        this.id = type + count;
    }

    public abstract void turnOn();

    public abstract void turnOff();

    public abstract String getStatus();

    protected void markTurnedOn() {
        if (!isOn) {
            isOn = true;
            lastOnTime = System.currentTimeMillis();
        }
    }

    protected void markTurnedOff() {
        if (isOn) {
            long now = System.currentTimeMillis();
            if (lastOnTime > 0) {
                double hoursOn = (now - lastOnTime) / 3_600_000.0;
                energyUsed += hoursOn * powerUsage;
            }
            isOn = false;
            lastOnTime = 0;
        }
    }

    public String getId() {
        return id;
    }

    public boolean isOn() {
        return isOn;
    }

    public double getEnergyUsed() {
        return energyUsed;
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public Room getRoom() {
        return room;
    }

    void setRoom(Room room) {
        this.room = room;
    }
}
