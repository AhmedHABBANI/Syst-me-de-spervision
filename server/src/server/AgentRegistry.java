package server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AgentRegistry {

    private final Map<String, AgentInfo> agents = new ConcurrentHashMap<>();

    // ===== HISTORIQUE DES METRIQUES (AJOUT) =====
    private final Map<String, List<MetricSample>> metricsHistory = new ConcurrentHashMap<>();

    private final SseManager sseManager;

    public AgentRegistry(SseManager sseManager) {
        this.sseManager = sseManager;
    }

    public AgentInfo getOrCreate(String agentId) {
        return agents.computeIfAbsent(agentId, AgentInfo::new);
    }

    // =====================================================
    // Appelé quand une métrique UDP arrive
    // =====================================================
    public void updateMetric(String agentId, MetricSample metric) {

        AgentInfo agent = getOrCreate(agentId);
        AgentState oldState = agent.getState();

        agent.updateMetric(metric);

        // ===== STOCKAGE HISTORIQUE (AJOUT) =====
        metricsHistory
                .computeIfAbsent(agentId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(metric);

        if (oldState != agent.getState()) {
            notifyState(agent);
        }
    }

    // =====================================================
    // Appelé par TCP alertes
    // =====================================================
    public void markAlert(String agentId) {
        AgentInfo agent = getOrCreate(agentId);
        agent.markAlert();
        notifyState(agent);
    }

    // =====================================================
    // Appelé par le monitor OFFLINE
    // =====================================================
    public void markOffline(String agentId) {
        AgentInfo agent = agents.get(agentId);
        if (agent != null) {
            agent.markOffline();
            notifyState(agent);
        }
    }

    private void notifyState(AgentInfo agent) {
        if (sseManager == null) return;

        sseManager.broadcast(
                SseEventType.AGENT_STATE,
                "{\"agentId\":\"" + agent.getAgentId() +
                        "\",\"state\":\"" + agent.getState().name() + "\"}"
        );
    }

    public Map<String, AgentInfo> getAgents() {
        return agents;
    }

    // =====================================================
    // ===== NOUVELLE METHODE POUR EXPORT =====
    // =====================================================
    public List<MetricSample> getMetricsForAgent(String agentId) {
        return metricsHistory.getOrDefault(agentId, Collections.emptyList());
    }
}
