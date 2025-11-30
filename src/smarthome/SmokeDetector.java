package smarthome;

public class SmokeDetector extends SmartDevice implements SensorDevice, Controllable {
    private boolean smokeDetected = false;

    public SmokeDetector() {
        super(0.0); 
    }

    @Override
    public void turnOn() {
        markTurnedOn();
    }

    @Override
    public void turnOff() {
        markTurnedOff();
        smokeDetected = false;
    }

    public void triggerSmoke() {
        if (isOn) {
            smokeDetected = true;
        }
    }

    public void resetSmoke() {
        smokeDetected = false;
    }

    @Override
    public boolean isTriggered() {
        return smokeDetected;
    }

    @Override
    public String getStatus() {
        if (!isOn) {
            return "SmokeDetector OFF";
        }
        return smokeDetected ? "SmokeDetector ALARM" : "SmokeDetector ON (monitoring)";
    }
}
