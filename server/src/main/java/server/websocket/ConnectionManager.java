package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, Session session) {
        connections.put(authToken, new Connection(authToken, session));
    }

    public void removeConnection(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludingAuthToken, Notification message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.getSession().isOpen()) {
                if (!connection.getAuthToken().equals(excludingAuthToken)) {
                    connection.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            connections.remove(connection.getAuthToken());
        }
    }
}
