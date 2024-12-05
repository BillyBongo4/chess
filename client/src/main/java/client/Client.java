package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.UserData;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.Connect;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;
    private boolean loggedIn = false;
    private String username;
    private String authToken;
    private String color;
    private int gameID = 0;
    private boolean observing = false;
    private ChessGame game = null;
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;

    public Client(String serverUrl) throws IOException, URISyntaxException, DeploymentException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        notificationHandler = new NotificationHandler();
        //ws = new WebSocketFacade(serverUrl, new NotificationHandler());
    }

    public String eval(String input) {
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
                case "redraw" -> redrawBoard();
                case "valid" -> validMoves(params);
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "leave" -> leave();
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

    private String buildBoard(ChessGame game, boolean isBlack, ChessPosition selectedPiecePosition, Collection<ChessMove> validMoves) {
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
                ChessPosition currentPosition = new ChessPosition(i + 1, j + 1);
                String bgColor;
                if (currentPosition.equals(selectedPiecePosition)) {
                    bgColor = SET_BG_COLOR_YELLOW;
                } else if (validMoves.stream().anyMatch(move -> move.getEndPosition().equals(currentPosition))) {
                    bgColor = SET_BG_COLOR_GREEN;
                } else {
                    bgColor = ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                }
                ChessPiece currPiece = game.getBoard().getPiece(currentPosition);
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

    private String outputBoard(ChessGame game, ChessPosition position, Collection<ChessMove> validMoves, String... params) {
        StringBuilder output = new StringBuilder();
        String labels = params[1].equals("black") ? "h  g  f  e  d  c  b  a" : "a  b  c  d  e  f  g  h";

        output.append(RESET_TEXT_COLOR);

        output.append(buildHeaderFooter(labels));
        output.append(buildBoard(game, params[1].equals("black"), position, validMoves));
        output.append(buildHeaderFooter(labels));

        output.append(RESET_BG_COLOR);
        return output.toString();
    }

    public String join(String... params) throws Exception {
        try {
            if (params.length == 2) {
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.sendCommand(new Connect(authToken, Integer.parseInt(params[0]), username, params[1]));
                game = server.joinGame(authToken, params[0], params[1]);
                color = params[1];
                gameID = Integer.parseInt(params[0]);
                return "";
                //return outputBoard(game, params);
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
            /*String[] newParams = Arrays.copyOf(params, params.length + 1);
            newParams[newParams.length - 1] = "white";*/
            ws.sendCommand(new Connect(authToken, Integer.parseInt(params[0]), username, "observer"));
            color = "observer";
            observing = true;
            game = server.observeGame(authToken, params[0]);
            return "";
            //return outputBoard(game, newParams);
        } catch (Exception e) {
            throw new Exception("Invalid id!");
        }
    }

    public String redrawBoard() throws Exception {
        game = server.observeGame(authToken, String.valueOf(gameID));
        return outputBoard(game, String.valueOf(gameID), color);
    }

    public String validMoves(String... params) throws Exception {
        game = server.observeGame(authToken, String.valueOf(gameID));
        ChessPosition position = parsePosition(params[0]);
        Collection<ChessMove> validMoves = game.validMoves(position);
        return outputBoard(game, position, validMoves, String.valueOf(gameID), color);
    }

    private ChessPosition parsePosition(String pos) {
        int row = Character.getNumericValue(pos.charAt(1));
        int col = pos.charAt(0) - 'a' + 1;
        return new ChessPosition(row, col);
    }

    public String makeMove(String... params) throws Exception {
        if (params.length == 2) {
            game = server.observeGame(authToken, String.valueOf(gameID));
            ChessMove move = new ChessMove(parsePosition(params[0]), parsePosition(params[1]), null);
            MakeMove moveCommand = new MakeMove(authToken, gameID, move, game, color);
            ws.sendCommand(moveCommand);
            //game = server.observeGame(authToken, String.valueOf(gameID));
            return "";
        }
        throw new IOException("Expected: <source> <destination>");
    }

    public String leave() throws IOException {
        Leave leaveCommand = new Leave(authToken, gameID, color);
        ws.sendCommand(leaveCommand);
        ws.closeSession();
        gameID = 0;
        color = "";
        observing = false;
        game = null;
        return "You've left the game";
    }

    public String resign() throws IOException {
        UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        ws.sendCommand(resignCommand);
        return "You've resigned";
    }

    public String logout() throws Exception {
        if (loggedIn) {
            server.logoutUser(authToken);
            loggedIn = false;
            authToken = null;
            username = null;
            gameID = 0;
            color = null;
            observing = false;
            game = null;
            ws.closeSession();
            return "Logged out successfully!";
        }
        return "You are not logged in.";
    }

    public String help() {
        if (!loggedIn) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - help - with possible commands
                    - quit - playing chess
                    """;
        } else if (gameID > 0 && !observing) {
            return """
                    - redraw - redraw board
                    - valid <PIECE POSITION> - valid moves for given piece
                    - move <PIECE POSITION> <DESTINATION> - make move
                    - resign - accept defeat (ends game)
                    - leave - leave the game
                    """;
        } else if (observing) {
            return """
                    - redraw - redraw board
                    - valid <PIECE POSITION> - valid moves for given piece
                    - leave - leave the game
                    """;
        } else {
            return """
                    - create <NAME> - a game
                    - list - games
                    - join <NUMBER> [WHITE|BLACK] - a game
                    - observe <NUMBER> - a game
                    - logout - when you are done
                    - help - with possible commands
                    """;
        }
    }

    public String getUsername() {
        return loggedIn ? username : "LOGGED_OUT";
    }
}
