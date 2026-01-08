package server;

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

    @Override
    public String toString() {
        return String.format(
            "CPU=%.2f%% | MEM=%.2f%% | DISK=%.2f%% | ts=%d",
            cpu, memory, disk, timestamp
        );
    }
}