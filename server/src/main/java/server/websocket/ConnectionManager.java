package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String username, Session session) {
        connections.put(username, new Connection(username, session));
    }

    public void removeConnection(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludingUsername, Notification message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.getSession().isOpen()) {
                if (!connection.getUsername().equals(excludingUsername)) {
                    connection.send(message.toString());
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            connections.remove(connection.getUsername());
        }
    }
}
