package agent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpServerTest {

    public static void main(String[] args) throws Exception {

        try (DatagramSocket socket = new DatagramSocket(9999)) {
            byte[] buffer = new byte[1024];

            System.out.println("Serveur UDP en écoute sur le port 9999...\n");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(
                        packet.getData(),
                        0,
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                System.out.println("Reçu : " + message);
            }
        }
    }
}
