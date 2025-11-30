package smarthome;

public class GarageDoor extends SmartDevice implements Controllable, EnergyConsumer {
    public GarageDoor() {
        super(0.3); 
    }

    @Override
    public void turnOn() {
        markTurnedOn(); 
    }

    @Override
    public void turnOff() {
        markTurnedOff(); 
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
