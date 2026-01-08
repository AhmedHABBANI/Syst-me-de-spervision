package agent;

public class AgentTest {

    public static void main(String[] args) throws InterruptedException {

        MetricsCollector collector = new MetricsCollector();

        System.out.println("Démarrage de la collecte des métriques...\n");

        while (true) {
            MetricSample sample = collector.collect();
            System.out.println(sample);
            Thread.sleep(2000);
        }
    }
}
