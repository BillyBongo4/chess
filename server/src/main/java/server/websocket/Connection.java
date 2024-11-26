package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    private String username;
    private Session session;

    public Connection(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public String getUsername() {
        return username;
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
