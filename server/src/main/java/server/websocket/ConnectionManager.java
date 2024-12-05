package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGame;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, int gameID, String color, Session session) {
        connections.put(authToken, new Connection(authToken, gameID, color, session));
    }

    public void removeConnection(String authToken) {
        connections.remove(authToken);
    }

    public void broadcastToOneUser(String authToken, ServerMessage message) throws IOException {
        for (var connection : connections.values()) {
            if (connection.session().isOpen()) {
                if (connection.authToken().equals(authToken)) {
                    connection.send(new Gson().toJson(message));
                }
            }
        }
    }

    public void broadcastToAllInGame(int gameId, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session().isOpen()) {
                if (gameId == connection.gameID()) {
                    if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGame loadGame = (LoadGame) message;
                        loadGame = new LoadGame(loadGame.getGame(), connection.color());
                        connection.send(new Gson().toJson(loadGame));
                    } else {
                        connection.send(new Gson().toJson(message));
                    }
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            connections.remove(connection.authToken());
        }
    }

    public void broadcastToAllElseInGame(String excludingAuthToken, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session().isOpen()) {
                if (!connection.authToken().equals(excludingAuthToken)) {
                    connection.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            connections.remove(connection.authToken());
        }
    }
}
