package smarthome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {
    private final String name;
    private final List<SmartDevice> devices = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addDevice(SmartDevice device) {
        devices.add(device);
        device.setRoom(this);
    }

    public void removeDevice(SmartDevice device) {
        devices.remove(device);
        device.setRoom(null);
    }

    public List<SmartDevice> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    public SmartDevice findDeviceById(String id) {
        for (SmartDevice device : devices) {
            if (device.getId().equalsIgnoreCase(id)) {
                return device;
            }
        }
        return null;
    }
}
