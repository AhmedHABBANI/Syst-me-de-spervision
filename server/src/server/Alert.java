package server;

public class Alert {

    private final long timestamp;
    private final String agentId;
    private final String metric;
    private final double value;
    private final String level;

    public Alert(long timestamp, String agentId, String metric, double value, String level) {
        this.timestamp = timestamp;
        this.agentId = agentId;
        this.metric = metric;
        this.value = value;
        this.level = level;
    }

    public long getTimestamp() { return timestamp; }
    public String getAgentId() { return agentId; }
    public String getMetric() { return metric; }
    public double getValue() { return value; }
    public String getLevel() { return level; }

    @Override
    public String toString() {
        return String.format(
            "ALERT[%s] agent=%s metric=%s value=%.2f ts=%d",
            level, agentId, metric, value, timestamp
        );
    }
}
