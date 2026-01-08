package server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SseManager {

    private final List<SseClient> clients = new CopyOnWriteArrayList<>();

    public void addClient(SseClient client) {
        clients.add(client);
        System.out.println("Client SSE connecté (" + clients.size() + ")");
    }

    public void removeClient(SseClient client) {
        clients.remove(client);
    }

    public void broadcast(SseEventType type, String json) {
        for (SseClient client : clients) {
            try {
                client.sendEvent(type, json);
            } catch (Exception e) {
                removeClient(client);
            }
        }
    }
}
