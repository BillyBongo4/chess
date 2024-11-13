import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String createUser(UserData user) throws Exception {
        var path = "/user";
        var result = makeRequest("POST", path, user, LinkedTreeMap.class, null);
        return (String) result.get("authToken");
    }

    public String loginUser(UserData user) throws Exception {
        var path = "/session";
        var result = makeRequest("POST", path, user, LinkedTreeMap.class, null);
        return (String) result.get("authToken");
    }

    public void logoutUser(String authToken) throws Exception {
        var path = "/session";
        makeRequest("DELETE", path, null, Object.class, authToken);
    }

    public String listGames(String authToken) throws Exception {
        var path = "/game";
        var result = makeRequest("GET", path, null, HashMap.class, authToken);
        Gson gson = new Gson();
        List<GameData> gameList = new ArrayList<>();
        List<LinkedTreeMap> games = (List<LinkedTreeMap>) result.get("games");
        for (LinkedTreeMap game : games) {
            GameData gameData = gson.fromJson(gson.toJson(game), GameData.class);
            gameList.add(gameData);
        }
        if (!gameList.isEmpty()) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < gameList.size(); i++) {
                output.append((i + 1) + ". " + gameList.get(i).gameName() + "\n");
                String whiteUsername = (gameList.get(i).whiteUsername() == null) ? "No Player" : gameList.get(i).whiteUsername();
                String blackUsername = (gameList.get(i).blackUsername() == null) ? "No Player" : gameList.get(i).blackUsername();
                output.append("    White: " + whiteUsername + "\n");
                output.append("    Black: " + blackUsername);

                if (i + 1 < gameList.size()) {
                    output.append("\n");
                }
            }
            return output.toString();
        } else {
            return "No games! Do 'create <NAME>' to create one!";
        }
    }

    public void createGame(String authToken, String name) throws Exception {
        var path = "/game";
        var game = new GameData(0, null, null, name, null);
        makeRequest("POST", path, game, GameData.class, authToken);
    }

    public ChessGame joinGame(String authToken, String id, String color) throws Exception {
        var path = "/game";
        JsonObject request = new JsonObject();
        request.addProperty("gameID", id);
        color = color.toUpperCase();
        request.addProperty("playerColor", color);
        return makeRequest("PUT", path, request, ChessGame.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception();
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
