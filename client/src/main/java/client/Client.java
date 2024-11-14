package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.UserData;

import java.util.Arrays;

import static ui.EscapeSequences.*;

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
        try {
            if (params.length == 3) {
                var newUser = new UserData(params[0], params[1], params[2]);
                authToken = server.createUser(newUser);
                loggedIn = true;
                username = params[0];
                return String.format("Logged in as %s", username);
            }
            throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().equals("Expected: register <USERNAME> <PASSWORD> <EMAIL>")) {
                throw e;
            } else {
                throw new Exception("User already exists!");
            }
        }
    }

    public String login(String... params) throws Exception {
        try {
            if (params.length == 2) {
                var user = new UserData(params[0], params[1], "");
                authToken = server.loginUser(user);
                loggedIn = true;
                username = params[0];
                return String.format("Logged in as %s", username);
            }
            throw new Exception("Expected: login <USERNAME> <PASSWORD>");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().equals("Expected: login <USERNAME> <PASSWORD>")) {
                throw e;
            } else {
                throw new Exception("Invalid username or password!");
            }
        }
    }

    public String create(String... params) throws Exception {
        if (params.length == 1) {
            server.createGame(authToken, params[0]);
            return "Created game: '" + params[0] + "'";
        }
        return "Expected: create <NAME>";
    }

    public String list() throws Exception {
        return server.listGames(authToken);
    }

    private String getPieceSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case ChessPiece.PieceType.ROOK:
                return "R";
            case ChessPiece.PieceType.KNIGHT:
                return "N";
            case ChessPiece.PieceType.BISHOP:
                return "B";
            case ChessPiece.PieceType.KING:
                return "K";
            case ChessPiece.PieceType.QUEEN:
                return "Q";
            default:
                return "P";
        }
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

    private String outputBoard(ChessGame game, String... params) {
        StringBuilder output = new StringBuilder();
        String labels = params[1].equals("black") ? "h  g  f  e  d  c  b  a" : "a  b  c  d  e  f  g  h";

        output.append(RESET_TEXT_COLOR);

        output.append(buildHeaderFooter(labels));
        //output.append(buildBoard(game, !params[1].equals("black")));
        output.append(buildBoard(game, params[1].equals("black")));
        output.append(buildHeaderFooter(labels));

        output.append(RESET_BG_COLOR);
        return output.toString();
    }

    public String join(String... params) throws Exception {
        try {
            if (params.length == 2) {
                var game = server.joinGame(authToken, params[0], params[1]);
                return outputBoard(game, params);
            }
            return "Expected: join <ID> <WHITE|BLACK>";
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().equals("Expected: join <ID>")) {
                throw e;
            } else {
                throw new Exception("Invalid id or color!");
            }
        }
    }

    public String observe(String... params) throws Exception {
        try {
            String[] newParams = Arrays.copyOf(params, params.length + 1);
            newParams[newParams.length - 1] = "white";
            var game = server.observeGame(authToken, params[0]);
            return outputBoard(game, newParams);
        } catch (Exception e) {
            throw new Exception("Invalid id!");
        }
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
