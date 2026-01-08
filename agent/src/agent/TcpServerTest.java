package agent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpServerTest {

    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Serveur TCP en écoute sur le port 8888...\n");

            while (true) {
                Socket client = serverSocket.accept();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));

                String message = in.readLine();
                System.out.println("ALERTE reçue : " + message);

                client.close();
            }
        }
    }
}
