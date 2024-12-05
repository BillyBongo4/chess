package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public record Connection(String authToken, int gameID, String color, Session session) {

    public void send(String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }
}
