package agent;

import java.util.List;

public class AgentAlertTest {

    public static void main(String[] args) throws Exception {

        String agentId = "agent-1";   // 🔹 Identité de l’agent

        MetricsCollector collector = new MetricsCollector();
        ThresholdEvaluator evaluator = new ThresholdEvaluator();
        TcpAlertClient client = new TcpAlertClient("localhost", 8888);

        System.out.println("Agent alertes TCP demarre...\n");

        while (true) {
            MetricSample sample = collector.collect();
            List<Alert> alerts = evaluator.evaluate(sample, agentId);

            for (Alert alert : alerts) {
                client.send(alert);
                System.out.println("ALERTE envoyee : " + alert);
            }

            Thread.sleep(2000);
        }
    }
}
