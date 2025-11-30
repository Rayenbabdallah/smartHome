package smarthome;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class CentralController {
    private final Home home;
    private HomeMode currentMode = HomeMode.HOME;
    private final List<AutomationRule> rules = new ArrayList<>();
    private final Map<String, Double> energyLedger = new HashMap<>();
    private double energyToday = 0.0;
    private double energyMonth = 50.0; // baseline so monthly bar is not empty
    private double currentConsumption = 0.0;
    private TextArea logArea;

    public CentralController(Home home) {
        this.home = home;
    }

    public HomeMode getCurrentMode() {
        return currentMode;
    }

    public void setMode(HomeMode mode) {
        currentMode = mode;
        log("Switching home mode to " + mode);
        for (SmartDevice device : home.getAllDevices()) {
            switch (mode) {
                case AWAY:
                    if (device instanceof Light || device instanceof SmartTV || device instanceof SmartSpeaker) {
                        turnOffDevice(device);
                    }
                    if (device instanceof DoorLock) {
                        turnOnDevice(device);
                    }
                    if (device instanceof Camera || device instanceof MotionSensor || device instanceof SmokeDetector) {
                        turnOnDevice(device);
                    }
                    if (device instanceof Thermostat) {
                        turnOffDevice(device);
                    }
                    break;
                case HOME:
                    if (device instanceof Light && "Living Room".equalsIgnoreCase(device.getRoom().getName())) {
                        turnOnDevice(device);
                    } else if (device instanceof Light) {
                        // leave others unchanged
                    }
                    if (device instanceof DoorLock) {
                        turnOffDevice(device);
                    }
                    if (device instanceof Camera || device instanceof MotionSensor) {
                        turnOffDevice(device);
                    }
                    if (device instanceof Thermostat) {
                        turnOnDevice(device);
                    }
                    break;
                case NIGHT:
                    if (device instanceof Light) {
                        if ("Hallway".equalsIgnoreCase(device.getRoom().getName())) {
                            turnOnDevice(device);
                            ((Light) device).setBrightness(30);
                        } else {
                            turnOffDevice(device);
                        }
                    }
                    if (device instanceof DoorLock) {
                        turnOnDevice(device);
                    }
                    if (device instanceof Camera || device instanceof MotionSensor) {
                        turnOnDevice(device);
                    }
                    if (device instanceof Thermostat) {
                        turnOnDevice(device);
                        ((Thermostat) device).setTargetTemperature(18.0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void turnAllLightsOn() {
        for (SmartDevice device : home.getAllDevices()) {
            if (device instanceof Light) {
                turnOnDevice(device);
            }
        }
        log("All lights turned ON.");
    }

    public void turnAllLightsOff() {
        for (SmartDevice device : home.getAllDevices()) {
            if (device instanceof Light) {
                turnOffDevice(device);
            }
        }
        log("All lights turned OFF.");
    }

    public void lockAllDoors() {
        for (SmartDevice device : home.getAllDevices()) {
            if (device instanceof DoorLock) {
                turnOnDevice(device);
            }
        }
        log("All doors locked.");
    }

    public void unlockAllDoors() {
        for (SmartDevice device : home.getAllDevices()) {
            if (device instanceof DoorLock) {
                turnOffDevice(device);
            }
        }
        log("All doors unlocked.");
    }

    public void turnOnDevice(SmartDevice device) {
        if (!device.isOn()) {
            device.turnOn();
            if (device instanceof EnergyConsumer) {
                currentConsumption += ((EnergyConsumer) device).getPowerConsumption();
            }
        }
    }

    public void turnOffDevice(SmartDevice device) {
        if (device.isOn()) {
            device.turnOff();
            if (device instanceof EnergyConsumer) {
                currentConsumption -= ((EnergyConsumer) device).getPowerConsumption();
                if (currentConsumption < 0) {
                    currentConsumption = 0;
                }
                double total = ((SmartDevice) device).getEnergyUsed();
                double prev = energyLedger.getOrDefault(device.getId(), 0.0);
                double delta = Math.max(0.0, total - prev);
                energyToday += delta;
                energyMonth += delta;
                energyLedger.put(device.getId(), total);
            }
        }
    }

    public void scheduleDeviceAction(SmartDevice device, LocalTime time, boolean turnOn) {
        AtomicBoolean executed = new AtomicBoolean(false);
        AutomationRule scheduleRule = new AutomationRule(
            ctrl -> !executed.get() && (LocalTime.now().isAfter(time) || LocalTime.now().equals(time)),
            () -> {
                if (turnOn) {
                    turnOnDevice(device);
                } else {
                    turnOffDevice(device);
                }
                executed.set(true);
                log(device.getId() + " scheduled action executed.");
            }
        );
        rules.add(scheduleRule);
        log("Scheduled " + device.getId() + " to turn " + (turnOn ? "ON" : "OFF") + " at " + time);
    }

    public void sensorTriggered(SensorDevice sensor) {
        SmartDevice device = (SmartDevice) sensor;
        log("Sensor triggered: " + device.getId());
        if (sensor instanceof MotionSensor && currentMode == HomeMode.NIGHT) {
            Room room = device.getRoom();
            for (SmartDevice d : room.getDevices()) {
                if (d instanceof Light) {
                    turnOnDevice(d);
                    log(" -> Motion at night, turning on " + d.getId());
                }
            }
            ((MotionSensor) sensor).resetMotion();
        }
        if (sensor instanceof SmokeDetector) {
            log(" -> Smoke detected! Turning off HVAC and unlocking doors.");
            for (SmartDevice d : home.getAllDevices()) {
                if (d instanceof Thermostat) {
                    turnOffDevice(d);
                }
                if (d instanceof DoorLock) {
                    turnOffDevice(d);
                }
            }
            ((SmokeDetector) sensor).resetSmoke();
        }
        evaluateRules();
    }

    public void addRule(AutomationRule rule) {
        rules.add(rule);
    }

    public void evaluateRules() {
        for (AutomationRule rule : rules) {
            rule.checkAndExecute(this);
        }
    }

    public double getCurrentConsumption() {
        return currentConsumption;
    }

    public double getEnergyToday() {
        return energyToday;
    }

    public double getEnergyMonth() {
        return energyMonth;
    }

    public SmartDevice findDeviceById(String id) throws DeviceNotFoundException {
        return home.findDeviceById(id);
    }

    public Home getHome() {
        return home;
    }

    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }

    public void log(String message) {
        System.out.println("[Controller] " + message);
        if (logArea != null) {
            Platform.runLater(() -> logArea.appendText(message + "\n"));
        }
    }
}
