package server;

import com.google.gson.Gson;
import model.UserData;
import service.RegisterService;
import spark.*;

public class Server {
    private final RegisterService registerService;

    public Server(RegisterService registerService) {
        this.registerService = registerService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object register(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), UserData.class);
        var existingUser = registerService.getUser("temp!!");
        return new Gson().toJson(user);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
