package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import model.GameData;
import model.UserData;
import server.websocket.WebSocketHandler;
import service.Service;
import service.ServiceException;
import spark.*;

public class Server {
    private final DataAccess dataAccess = new MySqlDataAccess();//MemoryDataAccess();
    private final Service service = new Service(dataAccess);
    private final WebSocketHandler webSocketHandler = new WebSocketHandler(this);
    private final Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game/observe", this::observeGame);
        Spark.delete("/db", this::clear);
        Spark.exception(ServiceException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String createUser(Request req, Response res) throws Exception {
        var user = serializer.fromJson(req.body(), UserData.class);
        var result = service.registerUser(user);
        return serializer.toJson(result);
    }

    private String loginUser(Request req, Response res) throws Exception {
        var user = serializer.fromJson(req.body(), UserData.class);
        var result = service.loginUser(user);
        return serializer.toJson(result);
    }

    private String logoutUser(Request req, Response res) throws Exception {
        var authToken = req.headers("Authorization");
        var result = service.logoutUser(authToken);
        return serializer.toJson(result);
    }

    private String listGames(Request req, Response res) throws Exception {
        var authToken = req.headers("Authorization");
        var result = service.listGames(authToken);
        return serializer.toJson(result);
    }

    private String createGame(Request req, Response res) throws Exception {
        var authToken = req.headers("Authorization");
        var gameData = serializer.fromJson(req.body(), GameData.class);

        var result = service.createGame(authToken, gameData.gameName());
        return serializer.toJson(result);
    }

    private String validateJsonField(JsonObject body, String fieldName, Response res) {
        if (!body.has(fieldName) || body.get(fieldName).isJsonNull()) {
            res.status(400); // Bad Request
            res.body("{\"message\": \"Bad Request: Missing or null " + fieldName + " field. Error: missing_" + fieldName.toLowerCase() + "\"}");
            return res.body();
        }
        return null;
    }

    private String joinGame(Request req, Response res) throws Exception {
        var authToken = req.headers("Authorization");
        JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();

        String validationError = validateJsonField(body, "playerColor", res);
        if (validationError != null) {
            return validationError;
        }
        validationError = validateJsonField(body, "gameID", res);
        if (validationError != null) {
            return validationError;
        }

        var playerColor = body.get("playerColor").getAsString();
        var gameID = body.get("gameID").getAsInt();

        var result = service.joinGame(authToken, gameID, playerColor);
        return serializer.toJson(result);
    }

    private String observeGame(Request req, Response res) throws Exception {
        var authToken = req.headers("Authorization");
        var gameID = Integer.parseInt(req.queryParams("gameID")); // Use queryParams for GET request
        var result = service.observeGame(authToken, gameID);
        return serializer.toJson(result);
    }

    public GameData getGameData(String authToken, int gameID) throws Exception {
        return service.getGameData(authToken, gameID);
    }

    public String getUsername(String authToken) throws Exception {
        return service.getUser(authToken);
    }

    public ChessGame updateChessGame(String authToken, int gameID, ChessGame game) throws Exception {
        return service.updateChessGame(authToken, gameID, game);
    }

    public void updateChessUsername(String authToken, int gameID, String color) throws Exception {
        String formattedColor = "WHITE";
        if (color.equals("black")) {
            formattedColor = "BLACK";
        }
        service.updateChessUsername(authToken, gameID, formattedColor);
    }

    private String clear(Request req, Response res) throws Exception {
        return serializer.toJson(service.clear());
    }

    private void exceptionHandler(ServiceException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.type("application/json");
        res.body("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
