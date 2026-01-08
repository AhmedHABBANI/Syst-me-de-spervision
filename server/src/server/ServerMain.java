package server;

public class ServerMain {

    public static void main(String[] args) {

        // === Managers ===
        SseManager sseManager = new SseManager();
        AgentRegistry registry = new AgentRegistry(sseManager);
        AlertStore alertStore = new AlertStore();
        MetricStore metricStore = new MetricStore(); // ✅ AJOUT OBLIGATOIRE

        // === Récepteurs ===
        new Thread(new UdpMetricReceiver(9999, registry, metricStore)).start();
        new Thread(new TcpAlertReceiver(8888, alertStore, registry, sseManager)).start();
        new Thread(new AgentStateMonitor(registry)).start();

        // === API REST + SSE ===
        try {
            new HttpApiServer(
                    registry,
                    alertStore,
                    metricStore,
                    sseManager
            ).start(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Serveur central démarré.");
    }
}
