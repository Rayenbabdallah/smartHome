package smarthome;

public class SmartSpeaker extends SmartDevice implements Controllable, EnergyConsumer {
    private int volume = 50;

    public SmartSpeaker() {
        super(0.02); // 20W
    }

    @Override
    public void turnOn() {
        markTurnedOn();
    }

    @Override
    public void turnOff() {
        markTurnedOff();
    }

    public void setVolume(int volume) {
        this.volume = Math.min(100, Math.max(0, volume));
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String getStatus() {
        return isOn ? "Speaker ON (vol " + volume + ")" : "Speaker OFF";
    }

    @Override
    public double getPowerConsumption() {
        return powerUsage;
    }
}
