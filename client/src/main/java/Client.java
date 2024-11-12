import model.UserData;

import java.util.Arrays;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;
    private boolean loggedIn = false;
    private String username;
    private String authToken;

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
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length == 3) {
            var newUser = new UserData(params[0], params[1], params[2]);
            authToken = server.createUser(newUser);
            loggedIn = true;
            username = params[0];
            return String.format("Logged in as %s", username);
        }
        throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws Exception {
        if (params.length == 2) {
            var user = new UserData(params[0], params[1], "");
            authToken = server.loginUser(user);
            loggedIn = true;
            username = params[0];
            return String.format("Logged in as %s", username);
        }
        throw new Exception("Expected: login <USERNAME> <PASSWORD>");
    }

    public String create(String... params) throws Exception {
        if (params.length == 1) {

            return "Created game: '" + params[0] + "'";
        }
        return "Expected: create <NAME>";
    }

    public String list() throws Exception {
        return server.listGames(authToken);
    }

    public String join(String... params) throws Exception {
        return "JOIN!";
    }

    public String observe(String... params) throws Exception {
        return "OBSERVE!";
    }

    public String logout() throws Exception {
        if (loggedIn) {
            server.logoutUser(authToken);
            loggedIn = false;
            authToken = null;
            username = null;
            return "Logged out successfully!";
        }
        return "You are not logged in.";
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

    public String getUsername() {
        return loggedIn ? username : "LOGGED_OUT";
    }
}
