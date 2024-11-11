import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String createUser(UserData user) throws Exception {
        var path = "/user";
        var result = makeRequest("POST", path, user, LinkedTreeMap.class);
        return (String) result.get("authToken");
    }

    public String loginUser(UserData user) throws Exception {
        var path = "/session";
        var result = makeRequest("POST", path, user, LinkedTreeMap.class);
        return (String) result.get("authToken");
    }

    public void logoutUser(String authToken, UserData user) throws Exception {
        var path = "/session";
        makeRequest("DELETE", path, user, void.class);
    }

    public void listGames() throws Exception {
        var path = "/game";
        makeRequest("GET", path, new GameData(0, null, null, "", new ChessGame()), GameData.class);
    }

    public void createGame() throws Exception {
    }

    public void joinGame() throws Exception {
    }

    public void clear() throws Exception {
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
