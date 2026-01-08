package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpAlertReceiver implements Runnable {

    private final int port;
    private final AlertStore store;
    private final AgentRegistry registry;
    private final SseManager sseManager;

    public TcpAlertReceiver(int port,
                            AlertStore store,
                            AgentRegistry registry,
                            SseManager sseManager) {
        this.port = port;
        this.store = store;
        this.registry = registry;
        this.sseManager = sseManager;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Serveur TCP (alertes) en écoute sur le port " + port);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        try (Socket c = client;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {

            String json = in.readLine();
            if (json == null || json.isBlank()) return;

            Alert alert = parseAlert(json);

            store.add(alert);
            registry.markAlert(alert.getAgentId());

            sseManager.broadcast(SseEventType.ALERT, alert.toString());

            System.out.println("Reçu → " + alert);

        } catch (Exception e) {
            System.err.println("Erreur TCP client: " + e.getMessage());
        }
    }

    private Alert parseAlert(String json) {
        long timestamp = extractLong(json, "timestamp");
        String agentId = extractString(json, "agentId");
        String metric = extractString(json, "metric");
        double value = extractDouble(json, "value");
        String level = extractString(json, "level");

        return new Alert(timestamp, agentId, metric, value, level);
    }

    private long extractLong(String json, String key) {
        return Long.parseLong(json.split("\"" + key + "\":")[1].split("[,}]")[0].trim());
    }

    private double extractDouble(String json, String key) {
        return Double.parseDouble(json.split("\"" + key + "\":")[1].split("[,}]")[0].trim());
    }

    private String extractString(String json, String key) {
        String part = json.split("\"" + key + "\":")[1].trim();
        int q1 = part.indexOf('"');
        int q2 = part.indexOf('"', q1 + 1);
        return part.substring(q1 + 1, q2);
    }
}
