package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return users.get(username);

    }

    @Override
    public UserData createUser(UserData userData) {
        users.put(userData.username(), userData);
        return userData;
    }

    @Override
    public AuthData createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public GameData[] listGames() {
        return games.values().toArray(new GameData[0]);
    }

    @Override
    public GameData createGame(GameData gameData) {
        games.put(gameData.gameId(), gameData);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public boolean checkColorUsername(int gameID, String color) {
        var game = games.get(gameID);

        if (color.equals("WHITE") && game.whiteUsername() == null) {
            return false;
        }
        return !color.equals("BLACK") || game.blackUsername() != null;
    }

    @Override
    public void updateGame(int gameID, String username, String color) {
        String whiteUsername = games.get(gameID).whiteUsername();
        String blackUsername = games.get(gameID).blackUsername();
        String gameName = games.get(gameID).gameName();
        ChessGame game = games.get(gameID).game();
        if (color.equals("WHITE")) {
            whiteUsername = username;
        } else {
            blackUsername = username;
        }

        games.remove(gameID);
        games.put(gameID, new GameData(gameID, whiteUsername, blackUsername, gameName, game));
    }
}
