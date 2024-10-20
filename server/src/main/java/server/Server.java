package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.Service;
import service.ServiceException;
import spark.*;

import javax.sql.rowset.serial.SerialException;

public class Server {
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final Service service = new Service(dataAccess);
    private final Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String createUser(Request req, Response res) throws Exception {
        var newUser = serializer.fromJson(req.body(), UserData.class);
        var result = service.registerUser(newUser);
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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
