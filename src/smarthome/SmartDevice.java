package smarthome;


public abstract class SmartDevice {
    
    protected final String id;
    
    protected boolean isOn = false;
    
    protected long lastOnTime = 0;
    
    protected double energyUsed = 0.0;
    
    protected final double powerUsage;
    
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
