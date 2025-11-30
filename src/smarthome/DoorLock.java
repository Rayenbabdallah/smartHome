package smarthome;

public class DoorLock extends SmartDevice implements Controllable {
    public DoorLock() {
        super(0.0);
    }

    @Override
    public void turnOn() {
        markTurnedOn(); // lock
    }

    @Override
    public void turnOff() {
        markTurnedOff(); // unlock
    }

    @Override
    public String getStatus() {
        return isOn ? "DoorLock LOCKED" : "DoorLock UNLOCKED";
    }
}
