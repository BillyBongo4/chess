package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    private String authToken;
    private Session session;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Session getSession() {
        return session;
    }

    public void send(String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }
}
