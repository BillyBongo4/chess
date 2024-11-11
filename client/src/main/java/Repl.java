import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        String loggedInStatus = "[LOGGED_OUT]";
        if (client.getLoggedInStatus()) {
            loggedInStatus = "[LOGGED_IN]";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + loggedInStatus + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
