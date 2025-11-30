package smarthome;

import java.time.LocalTime;

public class Thermostat extends SmartDevice implements Controllable, EnergyConsumer, Schedulable {
    private double targetTemperature;
    private LocalTime scheduledTime;

    public Thermostat(double targetTemperature) {
        super(0.01); 
        this.targetTemperature = targetTemperature;
    }

    @Override
    public void turnOn() {
        markTurnedOn();
    }

    @Override
    public void turnOff() {
        markTurnedOff();
    }

    public void setTargetTemperature(double targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public double getTargetTemperature() {
        return targetTemperature;
    }

    @Override
    public String getStatus() {
        return isOn ? "Thermostat ON (target " + targetTemperature + "C)" : "Thermostat OFF";
    }

    @Override
    public double getPowerConsumption() {
        return powerUsage;
    }

    @Override
    public void schedule(LocalTime time) {
        scheduledTime = time;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }
}
