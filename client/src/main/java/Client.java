import java.util.Arrays;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;
    private boolean loggedIn = false;
    private String username;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws Exception {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length >= 1) {
            loggedIn = true;
            username = params[0];
            return String.format("Logged in as %s", username);
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws Exception {
        if (params.length >= 1) {
            loggedIn = true;
            username = params[0];
            return String.format("Logged in as %s", username);
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    public String help() {
        if (!loggedIn) {
            return """
                    - register <USERNAMAE> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - help - with possible commands
                    - quit - playing chess
                    """;
        } else {
            return """
                    - create <NAME> - a game
                    - list - games
                    - join <ID> [WHITE|BLACK] - a game
                    - observe <ID> - a game
                    - logout - when you are done
                    - help - with possible commands
                    - quit
                    """;
        }
    }

    public boolean getLoggedInStatus() {
        return loggedIn;
    }
}
