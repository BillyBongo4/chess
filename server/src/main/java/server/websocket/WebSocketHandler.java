package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import service.ServiceException;
import websocket.commands.Connect;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final Server server;

    public WebSocketHandler(Server server) {
        this.server = server;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            Connect connect = null;
            MakeMove makeMove = null;
            Leave leave = null;
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                connect = new Gson().fromJson(message, Connect.class);
            } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                makeMove = new Gson().fromJson(message, MakeMove.class);
            } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                leave = new Gson().fromJson(message, Leave.class);
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
                case LEAVE -> {
                    if (leave != null) {
                        handleLeave(leave);
                    }
                }
            }
        } catch (Exception e) {
            onError(session, e);
        }
    }

    private void handleConnect(Session session, Connect command) throws Exception {
        try {
            ChessGame game = server.getGame(command.getAuthToken(), command.getGameID());
            if (game == null) {
                // Send an error notification to the user
                System.out.println("Invalid gameID detected.");
                connections.broadcastToOneUser(command.getAuthToken(), new Notification("Invalid gameID! Connection failed."));
                // Log the notification sent
                System.out.println("Notification sent for invalid gameID.");
                return;
            }

            connections.addConnection(command.getAuthToken(), command.getGameID(), command.getColor(), session);
            connections.broadcastToAllElseInGame(command.getAuthToken(), command.getGameID(), new Notification(command.getUsername() + " joined as " + command.getColor()));
            connections.broadcastToOneUser(command.getAuthToken(), new LoadGame(game, command.getColor()));
        } catch (ServiceException e) {
            // Catch the ServiceException and notify the user
            System.out.println("ServiceException caught in handleConnect: " + e.getMessage());
            connections.broadcastToOneUser(command.getAuthToken(), new Notification("Error: " + e.getMessage()));
        }
    }

    private void handleMakeMove(MakeMove command) throws Exception {
        if (isGameOver(command.getGame())) {
            notifyUser(command.getAuthToken(), "Can't make move! Game is over!");
            return;
        }

        ChessGame.TeamColor teamColor = getTeamColor(command.getColor());

        if (teamColor != command.getGame().getTeamTurn()) {
            notifyUser(command.getAuthToken(), "Not your turn!");
            return;
        }

        try {
            command.getGame().makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            notifyUser(command.getAuthToken(), "Invalid Move!");
            return;
        }

        ChessGame updatedGame = server.updateChessGame(command.getAuthToken(), command.getGameID(), command.getGame());
        String username = server.getUsername(command.getAuthToken());

        broadcastGameUpdate(command, updatedGame);
        notifyMove(command.getAuthToken(), command.getGameID(), username, command.getMove());
        notifyGameStatus(command.getGameID(), updatedGame);
    }

    private boolean isGameOver(ChessGame game) {
        return game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInStalemate(ChessGame.TeamColor.BLACK) ||
                game.getGameOver();
    }

    private ChessGame.TeamColor getTeamColor(String color) {
        return color.equalsIgnoreCase("black") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
    }

    private void notifyUser(String authToken, String message) throws Exception {
        connections.broadcastToOneUser(authToken, new Notification(message));
    }

    private void broadcastGameUpdate(MakeMove command, ChessGame game) throws Exception {
        LoadGame loadGame = new LoadGame(game, command.getColor());
        connections.broadcastToAllInGame(command.getGameID(), loadGame);
    }

    private String positionToString(ChessPosition position) {
        char col = (char) ('a' + position.getColumn() - 1);
        int row = position.getRow();
        return String.valueOf(col) + row;
    }

    private void notifyMove(String authToken, int gameID, String username, ChessMove move) throws Exception {
        String userChessMove = positionToString(move.getStartPosition()) + " -> " + positionToString(move.getEndPosition());
        connections.broadcastToAllElseInGame(authToken, gameID, new Notification(username + " moved " + userChessMove));
    }

    private void notifyGameStatus(int gameID, ChessGame game) throws Exception {
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            connections.broadcastToAllInGame(gameID, new Notification("Checkmate! Black wins!"));
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            connections.broadcastToAllInGame(gameID, new Notification("Checkmate! White wins!"));
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            connections.broadcastToAllInGame(gameID, new Notification("Stalemate! It's a Draw!"));
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            connections.broadcastToAllInGame(gameID, new Notification("White is in Check!"));
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            connections.broadcastToAllInGame(gameID, new Notification("Black is in Check!"));
        }
    }

    private void handleResign(UserGameCommand command) throws Exception {
        var game = server.getGame(command.getAuthToken(), command.getGameID());
        game.setGameOver(true);
        server.updateChessGame(command.getAuthToken(), command.getGameID(), game);
        var username = server.getUsername(command.getAuthToken());

        connections.broadcastToAllInGame(command.getGameID(), new Notification(username + " has resigned! Game over!"));
    }

    private void handleLeave(Leave command) throws Exception {
        var username = server.getUsername(command.getAuthToken());
        server.updateChessUsername(command.getAuthToken(), command.getGameID(), command.getColor());

        connections.broadcastToAllElseInGame(command.getAuthToken(), command.getGameID(), new Notification(username + " has left the game!"));
        connections.removeConnection(command.getAuthToken());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket Error: " + error.getMessage());
        error.printStackTrace();
        try {
            session.close(1011, "An error occurred: " + error.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
