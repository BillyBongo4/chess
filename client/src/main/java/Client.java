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
            server.createGame(authToken, params[0]);
            return "Created game: '" + params[0] + "'";
        }
        return "Expected: create <NAME>";
    }

    public String list() throws Exception {
        return server.listGames(authToken);
    }

    public String join(String... params) throws Exception {
        if (params.length == 2) {
            var game = server.joinGame(authToken, params[0], params[1]);

            StringBuilder output = new StringBuilder();

            output.append(RESET_TEXT_COLOR);
            if (params[1].equals("black")) {
                output.append(SET_BG_COLOR_DARK_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n");
                for (int i = 0; i < 8; i++) {
                    output.append(SET_BG_COLOR_DARK_GREY + " " + String.valueOf(i + 1) + " ");
                    for (int j = 0; j < 8; j++) {
                        String bgColor = ((i % 2 == 0 && j % 2 == 0) | (i % 2 != 0 && j % 2 != 0)) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                        ChessPiece currPiece = game.getBoard().getPiece(new ChessPosition(i + 1, j + 1));
                        if (currPiece != null) {
                            String textColor = (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED;
                            if (currPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                                output.append(bgColor + textColor + " R ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                                output.append(bgColor + textColor + " N ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                                output.append(bgColor + textColor + " B ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                output.append(bgColor + textColor + " K ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                                output.append(bgColor + textColor + " Q ");
                            } else {
                                output.append(bgColor + textColor + " P ");
                            }
                        } else {
                            output.append(bgColor + "   ");
                        }
                        output.append(RESET_TEXT_COLOR);
                    }
                    output.append(SET_BG_COLOR_DARK_GREY + " " + String.valueOf(i + 1) + " ");
                    output.append(RESET_BG_COLOR + "\n");
                }
                output.append(SET_BG_COLOR_DARK_GREY + "    h  g  f  e  d  c  b  a    ");
            } else if (params[1].equals("white")) {
                output.append(SET_BG_COLOR_DARK_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n");
                for (int i = 7; i >= 0; i--) {
                    output.append(SET_BG_COLOR_DARK_GREY + " " + String.valueOf(i + 1) + " ");
                    for (int j = 7; j >= 0; j--) {
                        String bgColor = ((i % 2 == 0 && j % 2 == 0) | (i % 2 != 0 && j % 2 != 0)) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                        ChessPiece currPiece = game.getBoard().getPiece(new ChessPosition(i + 1, j + 1));
                        if (currPiece != null) {
                            String textColor = (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED;
                            if (currPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                                output.append(bgColor + textColor + " R ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                                output.append(bgColor + textColor + " N ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                                output.append(bgColor + textColor + " B ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                output.append(bgColor + textColor + " K ");
                            } else if (currPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                                output.append(bgColor + textColor + " Q ");
                            } else {
                                output.append(bgColor + textColor + " P ");
                            }
                        } else {
                            output.append(bgColor + "   ");
                        }
                        output.append(RESET_TEXT_COLOR);
                    }
                    output.append(SET_BG_COLOR_DARK_GREY + " " + String.valueOf(i + 1) + " ");
                    output.append(RESET_BG_COLOR + "\n");
                }
                output.append(SET_BG_COLOR_DARK_GREY + "    a  b  c  d  e  f  g  h    ");
            }
            output.append(RESET_BG_COLOR);

            return output.toString();
        }
        return "Expected: join <ID> <WHITE|BLACK>";
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
