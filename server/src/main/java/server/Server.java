package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import model.GameData;
import model.UserData;
import service.Service;
import service.ServiceException;
import spark.*;

public class Server {
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final Service service = new Service(dataAccess);
    private final Gson serializer = new Gson();

    private UserData user = null;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.exception(ServiceException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String createUser(Request req, Response res) throws Exception {
        user = serializer.fromJson(req.body(), UserData.class);
        var result = service.registerUser(user);
        return serializer.toJson(result);
    }

    private String loginUser(Request req, Response res) throws Exception {
        user = serializer.fromJson(req.body(), UserData.class);
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

    private String joinGame(Request req, Response res) throws Exception {
        var authToken = req.headers("Authorization");
        JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
        var playerColor = body.get("playerColor").getAsString();
        var gameID = body.get("gameID").getAsInt();

        var result = service.joinGame(authToken, user, gameID, playerColor);
        return serializer.toJson(result);
    }

    private String clear(Request req, Response res) {
        return serializer.toJson(service.clear());
    }

    private void exceptionHandler(ServiceException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.type("application/json");
        res.body("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
