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
    public UserData getUser(String username) throws Exception {
        return users.get(username);

    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.username().isEmpty()) {
            throw new DataAccessException("Error: Invalid username");
        } else if (userData.password() == null || userData.password().isEmpty()) {
            throw new DataAccessException("Error: Invalid password");
        } else if (userData.email() == null || userData.email().isEmpty()) {
            throw new DataAccessException("Error: Invalid email");
        }

        users.put(userData.username(), userData);
        return userData;
    }

    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        if (authData.authToken() == null || authData.authToken().isEmpty()) {
            throw new DataAccessException("Error: Invalid authToken");
        } else if (authData.username() == null || authData.username().isEmpty()) {
            throw new DataAccessException("Error: Invalid username");
        }

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
    public GameData createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Error: Invalid gameName");
        }

        GameData gameData = new GameData(games.size() + 1, null, null, gameName, new ChessGame());
        games.put(gameData.gameId(), gameData);
        return gameData;
    }

    @Override
    public boolean checkColorUsername(int gameID, String color) throws DataAccessException {
        var game = games.get(gameID);

        if (game == null) {
            throw new DataAccessException("Game doesn't exist!");
        }

        if (color.equals("WHITE") && game.whiteUsername() == null) {
            return false;
        }
        return !color.equals("BLACK") || game.blackUsername() != null;
    }

    @Override
    public void updateGame(int gameID, String username, String color) throws DataAccessException {
        if (gameID > listGames().length) {
            throw new DataAccessException("Error: Invalid gameID");
        } else if (username.isEmpty()) {
            throw new DataAccessException("Error: Invalid username");
        } else if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new DataAccessException("Error: Invalid color");
        }

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

    @Override
    public void updateChessGame(int gameID, ChessGame game) throws DataAccessException {
        if (gameID > listGames().length) {
            throw new DataAccessException("Error: Invalid gameID");
        }

        String whiteUsername = games.get(gameID).whiteUsername();
        String blackUsername = games.get(gameID).blackUsername();
        String gameName = games.get(gameID).gameName();

        games.remove(gameID);
        games.put(gameID, new GameData(gameID, whiteUsername, blackUsername, gameName, game));
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (gameID > listGames().length) {
            throw new DataAccessException("Error: Invalid gameID");
        }

        return games.get(gameID);
    }

    @Override
    public void clearGameData() {
        games.clear();
    }

    @Override
    public void clearAuthData() {
        auths.clear();
    }

    @Override
    public void clearUserData() {
        users.clear();
    }
}
