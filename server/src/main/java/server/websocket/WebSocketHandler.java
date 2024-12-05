package server.websocket;

import chess.ChessGame;
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
        connections.addConnection(command.getAuthToken(), command.getGameID(), command.getColor(), session);
        connections.broadcastToAllElseInGame(command.getAuthToken(), new Notification(command.getUsername() + " joined as " + command.getColor()));
        connections.broadcastToOneUser(command.getAuthToken(), new LoadGame(server.getGame(command.getAuthToken(), command.getGameID()), command.getColor()));
    }

    private void handleMakeMove(MakeMove command) throws Exception {
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.WHITE;
        if (command.getColor().equals("black")) {
            teamColor = ChessGame.TeamColor.BLACK;
        }

        if (command.getGame().isInCheckmate(ChessGame.TeamColor.WHITE) || command.getGame().isInCheckmate(ChessGame.TeamColor.BLACK)
                || command.getGame().isInStalemate(ChessGame.TeamColor.WHITE) || command.getGame().isInStalemate(ChessGame.TeamColor.BLACK)) {
            connections.broadcastToOneUser(command.getAuthToken(), new Notification("Can't make move! Game is over!"));
        } else {
            if (teamColor == command.getGame().getTeamTurn()) {
                var result = server.updateChessGame(command.getAuthToken(), command.getGameID(), command.getGame(), command.getMove());
                var username = server.getUsername(command.getAuthToken());

                if (!result.equals("Invalid move!")) {
                    ChessGame updatedGame = new Gson().fromJson(result, ChessGame.class);

                    LoadGame loadGame = new LoadGame(updatedGame, command.getColor());
                    connections.broadcastToAllInGame(command.getGameID(), loadGame);
                    connections.broadcastToAllElseInGame(command.getAuthToken(), new Notification(username + " moved " + command.getMove().toString()));
                    if (updatedGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                        connections.broadcastToAllInGame(command.getGameID(), new Notification("Checkmate! Black wins!"));
                    } else if (updatedGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                        connections.broadcastToAllInGame(command.getGameID(), new Notification("Checkmate! White wins!"));
                    } else if (updatedGame.isInStalemate(ChessGame.TeamColor.WHITE) || updatedGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                        connections.broadcastToAllInGame(command.getGameID(), new Notification("Stalemate! It's a Draw!"));
                    } else if (updatedGame.isInCheck(ChessGame.TeamColor.WHITE)) {
                        connections.broadcastToAllInGame(command.getGameID(), new Notification("White is in Check!"));
                    } else if (updatedGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                        connections.broadcastToAllInGame(command.getGameID(), new Notification("Black is in Check!"));
                    }
                } else {
                    connections.broadcastToOneUser(command.getAuthToken(), new Notification(result));
                }
            } else {
                connections.broadcastToOneUser(command.getAuthToken(), new Notification("Not your turn!"));
            }
        }
    }

    private void handleResign(UserGameCommand command) {
        System.out.println("Resigned");
    }

    private void handleLeave(UserGameCommand command) {
        System.out.println("Left");
        connections.removeConnection(command.getAuthToken());
    }
}
