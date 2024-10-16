package server;

import com.google.gson.Gson;
import model.UserData;
import service.RegisterService;
import spark.*;
import exception.ResponseException;

public class Server {
    private RegisterService registerService;

    public Server() {
    }

    public Server(RegisterService registerService) {
        this.registerService = registerService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object register(Request request, Response response) throws ResponseException {
        var user = new Gson().fromJson(request.body(), UserData.class);
        var existingUser = registerService.getUser(user.getUsername());

        if (existingUser == null) {
            registerService.createUser(user);

            return new Gson().toJson(user);
        } else {
            throw new ResponseException(403, "Error: already taken");
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
