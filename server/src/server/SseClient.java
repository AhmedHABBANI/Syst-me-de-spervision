package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SseClient {

    private final OutputStream output;

    public SseClient(HttpExchange exchange) throws Exception {
        this.output = exchange.getResponseBody();
    }

    public synchronized void sendEvent(SseEventType type, String jsonData) throws Exception {
        String msg =
                "event: " + type.name().toLowerCase() + "\n" +
                "data: " + jsonData + "\n\n";

        output.write(msg.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }
}
