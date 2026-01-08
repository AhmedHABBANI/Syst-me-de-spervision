package agent;

import java.util.Locale;

public class MetricSample {

    private final long timestamp;
    private final double cpu;
    private final double memory;
    private final double disk;

    public MetricSample(long timestamp, double cpu, double memory, double disk) {
        this.timestamp = timestamp;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getCpu() {
        return cpu;
    }

    public double getMemory() {
        return memory;
    }

    public double getDisk() {
        return disk;
    }

    // =========================
    // JSON UDP (CORRIGÉ)
    // =========================
    public String toJson() {
        return String.format(Locale.US,
            "{\"timestamp\":%d,\"cpu\":%.2f,\"memory\":%.2f,\"disk\":%.2f}",
            timestamp, cpu, memory, disk
        );
    }

    @Override
    public String toString() {
        return toJson();
    }
}
