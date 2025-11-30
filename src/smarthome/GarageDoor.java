package smarthome;

public class GarageDoor extends SmartDevice implements Controllable, EnergyConsumer {
    public GarageDoor() {
        super(0.3); // 300W motor while moving
    }

    @Override
    public void turnOn() {
        markTurnedOn(); // open
    }

    @Override
    public void turnOff() {
        markTurnedOff(); // close
    }

    @Override
    public String getStatus() {
        return isOn ? "GarageDoor OPEN" : "GarageDoor CLOSED";
    }

    @Override
    public double getPowerConsumption() {
        return powerUsage;
    }
}
