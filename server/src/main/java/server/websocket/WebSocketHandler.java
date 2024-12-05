package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.Connect;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final Server server;

    public WebSocketHandler(Server server) {
        this.server = server;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        Connect connect = null;
        MakeMove makeMove = null;
        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            connect = new Gson().fromJson(message, Connect.class);
        } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMove = new Gson().fromJson(message, MakeMove.class);
        }
        switch (command.getCommandType()) {
            case CONNECT -> {
                if (connect != null) {
                    handleConnect(session, connect);
                }
            }
            case MAKE_MOVE -> {
                if (makeMove != null) {
                    handleMakeMove(makeMove);
                }
            }
            case RESIGN -> handleResign(command);
            case LEAVE -> handleLeave(command);
        }
    }

    private void handleConnect(Session session, Connect command) throws Exception {
        connections.addConnection(command.getAuthToken(), command.getGameID(), session);
        connections.broadcastToAllElseInGame(command.getAuthToken(), new Notification(command.getUsername() + " joined as " + command.getColor()));
        connections.loadGame(command.getAuthToken(), new LoadGame(server.getGame(command.getAuthToken(), command.getGameID()), command.getColor()));
    }

    private void handleMakeMove(MakeMove command) throws Exception {
        var updatedGame = server.updateChessGame(command.getAuthToken(), command.getGameID(), command.getGame());

        LoadGame loadGame = new LoadGame(updatedGame, command.getColor());
        connections.broadcastToAllInGame(command.getGameID(), loadGame);
        //connections.loadGame(command.getAuthToken(), loadGame);
    }

    private void handleResign(UserGameCommand command) {
        System.out.println("Resigned");
    }

    private void handleLeave(UserGameCommand command) {
        System.out.println("Left");
        connections.removeConnection(command.getAuthToken());
    }
}
