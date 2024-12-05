package websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public interface NotificationHandler {
    public void handleNotification(ServerMessage message);
}
