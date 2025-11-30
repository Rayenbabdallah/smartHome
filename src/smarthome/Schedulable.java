package smarthome;

import java.time.LocalTime;

/** Devices that can schedule a future action. */
public interface Schedulable {
    void schedule(LocalTime time);
}
