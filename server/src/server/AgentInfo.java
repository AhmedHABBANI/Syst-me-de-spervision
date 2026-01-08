package server;

public class AgentInfo {

    private final String agentId;
    private MetricSample lastMetric;
    private long lastSeen;
    private AgentState state;

    public AgentInfo(String agentId) {
        this.agentId = agentId;
        this.state = AgentState.INIT;
    }

    public synchronized void updateMetric(MetricSample metric) {
        this.lastMetric = metric;
        this.lastSeen = System.currentTimeMillis();

        if (state == AgentState.INIT || state == AgentState.OFFLINE) {
            state = AgentState.ONLINE;
        }
    }

    public synchronized void markAlert() {
        state = AgentState.ALERT;
    }

    public synchronized void markOffline() {
        state = AgentState.OFFLINE;
    }

    public String getAgentId() {
        return agentId;
    }

    public MetricSample getLastMetric() {
        return lastMetric;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public AgentState getState() {
        return state;
    }
}
