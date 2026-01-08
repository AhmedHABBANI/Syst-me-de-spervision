package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpMetricReceiver implements Runnable {

    private final int port;
    private final AgentRegistry registry;
    private final MetricStore metricStore;

    public UdpMetricReceiver(int port, AgentRegistry registry, MetricStore metricStore) {
        this.port = port;
        this.registry = registry;
        this.metricStore = metricStore;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {

            System.out.println("Serveur UDP en écoute sur le port " + port);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String json = new String(
                        packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8
                );

                MetricSample sample = parse(json);

                // pour l’instant agent unique
                String agentId = "agent-1";

                registry.updateMetric(agentId, sample);

                metricStore.update(agentId, sample);

                System.out.println(
                    "Metric reçue → " + agentId + " | " + sample
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MetricSample parse(String json) {
        long ts = extractLong(json, "timestamp");
        double cpu = extractDouble(json, "cpu");
        double mem = extractDouble(json, "memory");
        double disk = extractDouble(json, "disk");
        return new MetricSample(ts, cpu, mem, disk);
    }

    private long extractLong(String json, String key) {
        String v = json.split("\"" + key + "\":")[1].split("[,}]")[0];
        return Long.parseLong(v);
    }

    private double extractDouble(String json, String key) {
        String v = json.split("\"" + key + "\":")[1].split("[,}]")[0];
        return Double.parseDouble(v);
    }
}
