package smarthome;

public class DoorLock extends SmartDevice implements Controllable {
    public DoorLock() {
        super(0.0);
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
        return isOn ? "DoorLock LOCKED" : "DoorLock UNLOCKED";
    }
}
