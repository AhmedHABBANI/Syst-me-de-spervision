package server;

public class UdpServerMain {

    public static void main(String[] args) {

        AgentRegistry registry = new AgentRegistry(null);
        UdpMetricReceiver receiver = new UdpMetricReceiver(9999, registry, null);

        Thread udpThread = new Thread(receiver);
        udpThread.start();
    }
}
