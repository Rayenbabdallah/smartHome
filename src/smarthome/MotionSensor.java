package smarthome;

public class MotionSensor extends SmartDevice implements SensorDevice, Controllable {
    private boolean motionDetected = false;

    public MotionSensor() {
        super(0.0); 
    }

    @Override
    public void turnOn() {
        markTurnedOn(); 
    }

    @Override
    public void turnOff() {
        markTurnedOff(); 
        motionDetected = false;
    }

    public void triggerMotion() {
        if (isOn) {
            motionDetected = true;
        }
    }

    public void resetMotion() {
        motionDetected = false;
    }

    @Override
    public boolean isTriggered() {
        return motionDetected;
    }

    @Override
    public String getStatus() {
        if (!isOn) {
            return "MotionSensor OFF (disarmed)";
        }
        return motionDetected ? "MotionSensor TRIGGERED" : "MotionSensor ON (armed)";
    }
}
