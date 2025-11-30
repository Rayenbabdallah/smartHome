package smarthome;

public class Camera extends SmartDevice implements Controllable, EnergyConsumer {
    public Camera() {
        super(0.05); 
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
        return isOn ? "Camera ON (recording)" : "Camera OFF";
    }

    @Override
    public double getPowerConsumption() {
        return powerUsage;
    }
}
