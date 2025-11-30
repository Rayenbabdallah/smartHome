package smarthome;

/** Marks a device that can be controlled (turned on/off). */
public interface Controllable {
    void turnOn();
    void turnOff();
    String getStatus();
}
