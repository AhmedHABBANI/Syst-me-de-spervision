package agent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpMetricSender {

    private final String serverHost;
    private final int serverPort;

    public UdpMetricSender(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void send(MetricSample sample) throws Exception {

        byte[] data = sample.toJson().getBytes(StandardCharsets.UTF_8);

        InetAddress address = InetAddress.getByName(serverHost);
        DatagramPacket packet =
                new DatagramPacket(data, data.length, address, serverPort);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        }
    }
}
