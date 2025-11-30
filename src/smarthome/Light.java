package smarthome;

public class Light extends SmartDevice implements Controllable, EnergyConsumer {
    private int brightness;

    public Light(int brightness) {
        super(0.06); // 60W bulb -> 0.06 kW
        setBrightness(brightness);
    }

    @Override
    public void turnOn() {
        markTurnedOn();
        if (brightness == 0) {
            brightness = 100;
        }
    }

    @Override
    public void turnOff() {
        markTurnedOff();
    }

    public void setBrightness(int level) {
        brightness = Math.min(100, Math.max(0, level));
    }

    public int getBrightness() {
        return brightness;
    }

    @Override
    public String getStatus() {
        return isOn ? "Light ON (brightness " + brightness + "%)" : "Light OFF";
    }

    @Override
    public double getPowerConsumption() {
        return powerUsage;
    }
}
