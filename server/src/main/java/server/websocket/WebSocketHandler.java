package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
        if (isGameOver(command.getGame())) {
            notifyUser(command.getAuthToken(), "Can't make move! Game is over!");
            return;
        }

        ChessGame.TeamColor teamColor = getTeamColor(command.getColor());

        if (teamColor != command.getGame().getTeamTurn()) {
            notifyUser(command.getAuthToken(), "Not your turn!");
            return;
        }

        String result = server.updateChessGame(command.getAuthToken(), command.getGameID(), command.getGame(), command.getMove());
        if (result.equals("Invalid move!")) {
            notifyUser(command.getAuthToken(), result);
            return;
        }

        ChessGame updatedGame = new Gson().fromJson(result, ChessGame.class);
        String username = server.getUsername(command.getAuthToken());

        broadcastGameUpdate(command, updatedGame);
        notifyMove(command.getAuthToken(), username, command.getMove());
        notifyGameStatus(command.getGameID(), updatedGame);
    }

    private boolean isGameOver(ChessGame game) {
        return game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInStalemate(ChessGame.TeamColor.BLACK);
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


    private void notifyMove(String authToken, String username, ChessMove move) throws Exception {
        String userChessMove = positionToString(move.getStartPosition()) + " -> " + positionToString(move.getEndPosition());
        connections.broadcastToAllElseInGame(authToken, new Notification(username + " moved " + userChessMove));
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

    private void handleResign(UserGameCommand command) {
        System.out.println("Resigned");
    }

    private void handleLeave(UserGameCommand command) {
        System.out.println("Left");
        connections.removeConnection(command.getAuthToken());
    }
}
