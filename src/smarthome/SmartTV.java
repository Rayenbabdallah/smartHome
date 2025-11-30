package smarthome;

public class SmartTV extends SmartDevice implements Controllable, EnergyConsumer {
    private int channel = 1;
    private int volume = 50;

    public SmartTV() {
        super(0.1); 
    }

    @Override
    public void turnOn() {
        markTurnedOn();
    }

    @Override
    public void turnOff() {
        markTurnedOff();
    }

    public void setChannel(int channel) {
        this.channel = Math.max(1, channel);
    }

    public int getChannel() {
        return channel;
    }

    public void setVolume(int volume) {
        this.volume = Math.min(100, Math.max(0, volume));
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String getStatus() {
        return isOn ? "TV ON (channel " + channel + ", volume " + volume + ")" : "TV OFF";
    }

    @Override
    public double getPowerConsumption() {
        return powerUsage;
    }
}
