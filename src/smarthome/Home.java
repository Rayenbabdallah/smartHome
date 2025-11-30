package smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home {
    private final List<Room> rooms = new ArrayList<>();
    private final Map<String, SmartDevice> devicesById = new HashMap<>();

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
        for (SmartDevice device : room.getDevices()) {
            devicesById.remove(device.getId());
        }
    }

    public void addDeviceToRoom(SmartDevice device, Room room) {
        room.addDevice(device);
        devicesById.put(device.getId(), device);
    }

    public void removeDevice(SmartDevice device) {
        Room room = device.getRoom();
        if (room != null) {
            room.removeDevice(device);
        }
        devicesById.remove(device.getId());
    }

    public SmartDevice findDeviceById(String id) throws DeviceNotFoundException {
        SmartDevice device = devicesById.get(id);
        if (device == null) {
            throw new DeviceNotFoundException("No device found with ID: " + id);
        }
        return device;
    }

    public List<SmartDevice> findDevicesByType(String typeName) {
        List<SmartDevice> matches = new ArrayList<>();
        String target = typeName.toLowerCase();
        for (Room room : rooms) {
            for (SmartDevice device : room.getDevices()) {
                if (device.getClass().getSimpleName().toLowerCase().equals(target)) {
                    matches.add(device);
                }
            }
        }
        return matches;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<SmartDevice> getAllDevices() {
        List<SmartDevice> all = new ArrayList<>();
        for (Room room : rooms) {
            all.addAll(room.getDevices());
        }
        return all;
    }
}
