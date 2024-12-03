package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMakeMove(command);
            case RESIGN -> handleResign(command);
            case LEAVE -> handleLeave(command);
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        connections.addConnection(command.getAuthToken(), session);
    }

    private void handleMakeMove(UserGameCommand command) {
    }

    private void handleResign(UserGameCommand command) {
    }

    private void handleLeave(UserGameCommand command) {
    }
}
