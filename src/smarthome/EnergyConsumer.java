package smarthome;

/** Devices that consume measurable power. */
public interface EnergyConsumer {
    double getPowerConsumption();

    default double getEnergyConsumed() {
        return (this instanceof SmartDevice) ? ((SmartDevice) this).getEnergyUsed() : 0.0;
    }
}
