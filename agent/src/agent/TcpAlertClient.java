package agent;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpAlertClient {

    private final String serverHost;
    private final int serverPort;

    public TcpAlertClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void send(Alert alert) throws Exception {

        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                     true)) {

            out.println(alert.toJson());
        }
    }
}
