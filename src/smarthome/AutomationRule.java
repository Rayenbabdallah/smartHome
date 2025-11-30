package smarthome;

import java.util.function.Predicate;


public class AutomationRule {
    private final Predicate<CentralController> condition;
    private final Runnable action;

    public AutomationRule(Predicate<CentralController> condition, Runnable action) {
        this.condition = condition;
        this.action = action;
    }

    public void checkAndExecute(CentralController controller) {
        try {
            if (condition.test(controller)) {
                action.run();
            }
        } catch (Exception ex) {
            System.err.println("Error executing automation rule: " + ex.getMessage());
        }
    }
}
