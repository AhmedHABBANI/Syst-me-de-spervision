package server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MetricStore {

    // === DONNÉES EXISTANTES (NE PAS CASSER) ===
    private final Map<String, MetricSample> latestMetrics = new ConcurrentHashMap<>();

    // === NOUVEAU : HISTORIQUE ===
    private final Map<String, List<MetricSample>> history = new ConcurrentHashMap<>();

    // =====================================================
    // Utilisé partout dans le projet (UDP)
    // =====================================================
    public void update(String agentId, MetricSample sample) {
        latestMetrics.put(agentId, sample);

        history
            .computeIfAbsent(agentId,
                    k -> Collections.synchronizedList(new ArrayList<>()))
            .add(sample);
    }

    // =====================================================
    // API REST /metrics/{id}
    // =====================================================
    public MetricSample getLatest(String agentId) {
        return latestMetrics.get(agentId);
    }

    // =====================================================
    // API REST globale (déjà utilisée)
    // =====================================================
    public Map<String, MetricSample> getAll() {
        return latestMetrics;
    }

    // =====================================================
    // NOUVEAU : historique complet
    // =====================================================
    public List<MetricSample> getHistory(String agentId) {
        return history.getOrDefault(agentId, List.of());
    }

    // =====================================================
    // NOUVEAU : EXPORT CSV
    // =====================================================
    public String exportCsv(String agentId) {

        List<MetricSample> list = getHistory(agentId);

        String header = "timestamp,cpu,memory,disk\n";

        String body = list.stream()
                .map(m -> String.format(
                        "%d,%.2f,%.2f,%.2f",
                        m.getTimestamp(),
                        m.getCpu(),
                        m.getMemory(),
                        m.getDisk()
                ))
                .collect(Collectors.joining("\n"));

        return header + body;
    }
}
