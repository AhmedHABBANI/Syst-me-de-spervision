package server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class AlertStore {

    private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<>());

    public void add(Alert alert) {
        alerts.add(alert);
    }

    public List<Alert> getAll() {
        // on renvoie une copie pour éviter les soucis de concurrence
        synchronized (alerts) {
            return new ArrayList<>(alerts);
        }
    }
}
