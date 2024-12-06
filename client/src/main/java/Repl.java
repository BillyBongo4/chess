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
                //System.out.println("\n" + outputBoard(loadGame.getGame(), loadGame.getColor()));
                System.out.println("\n" + client.outputBoard(loadGame.getGame(), loadGame.getColor()));
                if (inGame) {
                    printPrompt();
                }
                break;
            case ERROR:
                Notification errorMessage = (Notification) message;
                System.out.println("Error: " + errorMessage.getMessage());
                Notification errorNotification = new Notification("Error: " + errorMessage.getMessage());
                System.out.println(errorNotification.getMessage());
                printPrompt();
                break;
            case NOTIFICATION:
                Notification notification = (Notification) message;
                System.out.println(notification.getMessage());
                printPrompt();
                break;
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + client.getUsername() + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
