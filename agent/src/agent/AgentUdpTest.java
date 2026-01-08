package agent;

public class AgentUdpTest {

    public static void main(String[] args) throws Exception {

        MetricsCollector collector = new MetricsCollector();
        UdpMetricSender sender = new UdpMetricSender("localhost", 9999);

        System.out.println("Agent UDP démarre...\n");

        while (true) {
            MetricSample sample = collector.collect();
            sender.send(sample);
            System.out.println("Envoye : " + sample.toJson());
            Thread.sleep(2000);
        }
    }
}
