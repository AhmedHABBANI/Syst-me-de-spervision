package agent;

import java.util.ArrayList;
import java.util.List;

public class ThresholdEvaluator {

    public List<Alert> evaluate(MetricSample sample, String agentId) {

        List<Alert> alerts = new ArrayList<>();
        long ts = sample.getTimestamp();

        // CPU
        if (sample.getCpu() >= Thresholds.CPU_CRITICAL) {
            alerts.add(new Alert(
                    ts,
                    agentId,
                    "CPU",
                    sample.getCpu(),
                    "CRITICAL"
            ));
        }

        // MEMORY
        if (sample.getMemory() >= Thresholds.MEM_CRITICAL) {
            alerts.add(new Alert(
                    ts,
                    agentId,
                    "MEMORY",
                    sample.getMemory(),
                    "CRITICAL"
            ));
        }

        // DISK
        if (sample.getDisk() >= Thresholds.DISK_CRITICAL) {
            alerts.add(new Alert(
                    ts,
                    agentId,
                    "DISK",
                    sample.getDisk(),
                    "CRITICAL"
            ));
        }

        return alerts;
    }
}
