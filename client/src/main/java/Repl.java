import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.Client;
import websocket.NotificationHandler;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.DeploymentException;

import static ui.EscapeSequences.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final Client client;
    private boolean inGame = false;

    public Repl(String serverUrl) throws IOException, URISyntaxException, DeploymentException {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (!inGame) {
                printPrompt();
            }
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result != null) {

                    if (result.equals("joined game")) {
                        inGame = true;
                        result = "";
                        printPrompt();
                    } else if (result.equals("You've left the game")) {
                        inGame = false;
                    }
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    @Override
    public void handleNotification(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                LoadGame loadGame = (LoadGame) message;
                System.out.println("\n" + outputBoard(loadGame.getGame(), loadGame.getColor()));
                if (inGame) {
                    printPrompt();
                }
                break;
            case ERROR:
                System.out.println("Error!");
                break;
            case NOTIFICATION:
                Notification notification = (Notification) message;
                System.out.println(notification.getMessage());
                printPrompt();
                break;
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case ChessPiece.PieceType.ROOK -> "R";
            case ChessPiece.PieceType.KNIGHT -> "N";
            case ChessPiece.PieceType.BISHOP -> "B";
            case ChessPiece.PieceType.KING -> "K";
            case ChessPiece.PieceType.QUEEN -> "Q";
            default -> "P";
        };
    }

    private String buildHeaderFooter(String labels) {
        return SET_BG_COLOR_DARK_GREY + "    " + labels + "    " + RESET_BG_COLOR + "\n";
    }

    private String buildBoard(ChessGame game, boolean isBlack) {
        StringBuilder output = new StringBuilder();
        int startRow = isBlack ? 0 : 7;
        int endRow = isBlack ? 8 : -1;
        int rowStep = isBlack ? 1 : -1;
        int startCol = isBlack ? 7 : 0;
        int endCol = isBlack ? -1 : 8;
        int colStep = isBlack ? -1 : 1;

        for (int i = startRow; i != endRow; i += rowStep) {
            output.append(SET_BG_COLOR_DARK_GREY).append(" ").append(i + 1).append(" ");
            for (int j = startCol; j != endCol; j += colStep) {
                String bgColor = ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                ChessPiece currPiece = game.getBoard().getPiece(new ChessPosition(i + 1, j + 1));
                if (currPiece != null) {
                    String textColor = (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED;
                    String pieceSymbol = getPieceSymbol(currPiece);
                    output.append(bgColor).append(textColor).append(" ").append(pieceSymbol).append(" ");
                } else {
                    output.append(bgColor).append("   ");
                }
                output.append(RESET_TEXT_COLOR);
            }
            output.append(SET_BG_COLOR_DARK_GREY).append(" ").append(i + 1).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        return output.toString();
    }

    private String outputBoard(ChessGame game, String color) {
        StringBuilder output = new StringBuilder();
        String labels = color.equals("black") ? "h  g  f  e  d  c  b  a" : "a  b  c  d  e  f  g  h";

        output.append(RESET_TEXT_COLOR);

        output.append(buildHeaderFooter(labels));
        //output.append(buildBoard(game, !params[1].equals("black")));
        output.append(buildBoard(game, color.equals("black")));
        output.append(buildHeaderFooter(labels));

        output.append(RESET_BG_COLOR);
        return output.toString();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + client.getUsername() + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
