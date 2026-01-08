package agent;

public class Alert {

    private final long timestamp;
    private final String agentId;
    private final String type;
    private final double value;
    private final String level;

    public Alert(long timestamp, String agentId, String type, double value, String level) {
        this.timestamp = timestamp;
        this.agentId = agentId;
        this.type = type;
        this.value = value;
        this.level = level;
    }

    public String toJson() {
        return String.format(
            "{\"timestamp\":%d,\"agentId\":\"%s\",\"metric\":\"%s\",\"value\":%.2f,\"level\":\"%s\"}",
            timestamp, agentId, type, value, level
        );
    }

    @Override
    public String toString() {
        return toJson();
    }
}
