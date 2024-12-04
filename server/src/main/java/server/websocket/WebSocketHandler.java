package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

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

    private void handleConnect(Session session, UserGameCommand command) throws Exception {
        connections.addConnection(command.getAuthToken(), session);
        Notification notification = new Notification("Loading");
        connections.broadcast(command.getAuthToken(), notification);
    }

    private void handleMakeMove(UserGameCommand command) {
        System.out.println("Made move");
    }

    private void handleResign(UserGameCommand command) {
        System.out.println("Resigned");
    }

    private void handleLeave(UserGameCommand command) {
        System.out.println("Left");
    }
}
