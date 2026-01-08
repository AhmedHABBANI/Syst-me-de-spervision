package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpApiServer {

    private final AgentRegistry registry;
    private final AlertStore alertStore;
    private final MetricStore metricStore;   // ← AJOUT
    private final SseManager sseManager;

    public HttpApiServer(AgentRegistry registry,
                         AlertStore alertStore,
                         MetricStore metricStore,   // ← AJOUT
                         SseManager sseManager) {
        this.registry = registry;
        this.alertStore = alertStore;
        this.metricStore = metricStore;
        this.sseManager = sseManager;
    }

    public void start(int port) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/export/metrics", this::handleExportMetrics);


        server.createContext("/agents", this::handleAgents);
        server.createContext("/alerts", this::handleAlerts);
        server.createContext("/events", this::handleSse);
        server.createContext("/metrics", this::handleMetrics); // ← AJOUT

        server.setExecutor(null);
        server.start();

        System.out.println("API REST + SSE démarrée sur http://localhost:" + port);
    }

    // =====================================================
    // /agents et /agents/{id}
    // =====================================================
    private void handleAgents(HttpExchange exchange) {
        try {
            if (handleOptions(exchange)) return;

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                send(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length == 2) {
                String response = registry.getAgents().values().stream()
                        .map(agent -> JsonUtils.obj(
                                JsonUtils.field("agentId", JsonUtils.quote(agent.getAgentId())),
                                JsonUtils.field("state", JsonUtils.quote(agent.getState().name())),
                                JsonUtils.field("lastSeen", String.valueOf(agent.getLastSeen()))
                        ))
                        .collect(Collectors.joining(",", "[", "]"));

                send(exchange, 200, response);
                return;
            }

            if (parts.length == 3) {
                String id = parts[2];
                AgentInfo agent = registry.getAgents().get(id);

                if (agent == null) {
                    send(exchange, 404, "{\"error\":\"Agent not found\"}");
                    return;
                }

                String json = JsonUtils.obj(
                        JsonUtils.field("agentId", JsonUtils.quote(agent.getAgentId())),
                        JsonUtils.field("state", JsonUtils.quote(agent.getState().name())),
                        JsonUtils.field("lastSeen", String.valueOf(agent.getLastSeen()))
                );

                send(exchange, 200, json);
                return;
            }

            send(exchange, 404, "{\"error\":\"Not found\"}");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // /alerts
    // =====================================================
    private void handleAlerts(HttpExchange exchange) {
        try {
            if (handleOptions(exchange)) return;

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                send(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String response = alertStore.getAll().stream()
                    .map(alert -> JsonUtils.obj(
                            JsonUtils.field("agentId", JsonUtils.quote(alert.getAgentId())),
                            JsonUtils.field("metric", JsonUtils.quote(alert.getMetric())),
                            JsonUtils.field("value", String.valueOf(alert.getValue())),
                            JsonUtils.field("level", JsonUtils.quote(alert.getLevel())),
                            JsonUtils.field("timestamp", String.valueOf(alert.getTimestamp()))
                    ))
                    .collect(Collectors.joining(",", "[", "]"));

            send(exchange, 200, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // /metrics/{id}  ← NOUVEAU
    // =====================================================
    private void handleMetrics(HttpExchange exchange) {
        try {
            if (handleOptions(exchange)) return;

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                send(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length != 3) {
                send(exchange, 400, "{\"error\":\"Bad request\"}");
                return;
            }

            String agentId = parts[2];
            MetricSample sample = metricStore.getLatest(agentId);

            if (sample == null) {
                send(exchange, 404, "{\"error\":\"No metrics for agent\"}");
                return;
            }

            String json = JsonUtils.obj(
                    JsonUtils.field("agentId", JsonUtils.quote(agentId)),
                    JsonUtils.field("timestamp", String.valueOf(sample.getTimestamp())),
                    JsonUtils.field("cpu", String.valueOf(sample.getCpu())),
                    JsonUtils.field("memory", String.valueOf(sample.getMemory())),
                    JsonUtils.field("disk", String.valueOf(sample.getDisk()))
            );

            send(exchange, 200, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// =====================================================
// /export/metrics/{agentId}  (CSV)
// =====================================================
    private void handleExportMetrics(HttpExchange exchange) {
        try {
            if (handleOptions(exchange)) return;

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length != 4) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            String agentId = parts[3];
            String csv = metricStore.exportCsv(agentId);

            byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add("Content-Type", "text/csv");
            exchange.getResponseHeaders().add(
                    "Content-Disposition",
                    "attachment; filename=\"metrics_" + agentId + ".csv\""
            );
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // /events (SSE)
    // =====================================================
    private void handleSse(HttpExchange exchange) {
        try {
            if (handleOptions(exchange)) return;

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Cache-Control", "no-cache");
            exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            exchange.getResponseHeaders().add("Connection", "keep-alive");

            exchange.sendResponseHeaders(200, 0);
            sseManager.addClient(new SseClient(exchange));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // CORS OPTIONS
    // =====================================================
    private boolean handleOptions(HttpExchange exchange) throws Exception {
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    // =====================================================
    // SEND JSON
    // =====================================================
    private void send(HttpExchange exchange, int status, String body) throws Exception {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
