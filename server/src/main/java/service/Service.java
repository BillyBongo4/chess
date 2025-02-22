package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Service {
    DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void validateUserData(UserData user) throws ServiceException {
        if (user == null || user.username() == null || user.password() == null) {
            throw new ServiceException(400, "Bad Request: Missing required fields. Error: missing_fields");
        }
    }

    private AuthData createAuth(UserData user) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), user.username());
        try {
            dataAccess.createAuth(authData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return authData;
    }

    public AuthData registerUser(UserData newUser) throws Exception {
        validateUserData(newUser);
        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException(403, "Error: User already exists");
        }

        String hashPassword = BCrypt.hashpw(newUser.password(), BCrypt.gensalt());

        UserData hashedUser = new UserData(newUser.username(), hashPassword, newUser.email());

        dataAccess.createUser(hashedUser);

        return createAuth(hashedUser);
    }

    public AuthData loginUser(UserData user) throws Exception {
        validateUserData(user);
        if (dataAccess.getUser(user.username()) == null) {
            throw new ServiceException(401, "Error: User doesn't exists");
        } else if (!BCrypt.checkpw(user.password(), dataAccess.getUser(user.username()).password())) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        return createAuth(user);
    }

    public String getUser(String authToken) throws Exception {
        validateAuth(authToken);

        return dataAccess.getAuth(authToken).username();
    }

    public String logoutUser(String authToken) throws Exception {
        validateAuth(authToken);

        dataAccess.deleteAuth(authToken);

        return null;
    }

    public Map<String, GameListEntry[]> listGames(String authToken) throws Exception {
        validateAuth(authToken);

        GameData[] games = dataAccess.listGames();
        GameListEntry[] entries = new GameListEntry[games.length];
        for (int i = 0; i < games.length; i++) {
            GameData game = games[i];
            entries[i] = new GameListEntry(
                    game.gameId(),
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername()
            );
        }
        Map<String, GameListEntry[]> result = new HashMap<>();
        result.put("games", entries);
        return result;
    }

    public Map<String, Integer> createGame(String authToken, String gameName) throws Exception {
        validateAuth(authToken);

        var gameData = dataAccess.createGame(gameName);

        Map<String, Integer> result = new HashMap<>();
        result.put("gameID", gameData.gameId());
        return result;
    }

    public ChessGame joinGame(String authToken, int gameID, String playerColor) throws Exception {
        validateAuth(authToken);
        validateGameID(authToken, gameID);

        if (dataAccess.checkColorUsername(gameID, playerColor)) {
            throw new ServiceException(403, "Error: Already taken");
        }

        UserData user = dataAccess.getUser(dataAccess.getAuth(authToken).username());
        validateUserData(user);

        dataAccess.updateGame(gameID, user.username(), playerColor);

        return dataAccess.getGame(gameID).game();
    }

    public GameData getGameData(String authToken, int gameID) throws Exception {
        validateAuth(authToken);
        validateGameID(authToken, gameID);

        UserData user = dataAccess.getUser(dataAccess.getAuth(authToken).username());
        validateUserData(user);

        return dataAccess.getGame(gameID);
    }

    public ChessGame observeGame(String authToken, int gameID) throws Exception {
        validateAuth(authToken);
        validateGameID(authToken, gameID);

        UserData user = dataAccess.getUser(dataAccess.getAuth(authToken).username());
        validateUserData(user);

        return dataAccess.getGame(gameID).game();
    }

    public ChessGame updateChessGame(String authToken, int gameID, ChessGame game) throws Exception {
        validateAuth(authToken);
        validateGameID(authToken, gameID);

        if (game == null) {
            throw new ServiceException(401, "Error: Game null");
        }

        dataAccess.updateChessGame(gameID, game);

        return dataAccess.getGame(gameID).game();
    }

    public void updateChessUsername(String authToken, int gameID, String color) throws Exception {
        validateAuth(authToken);

        dataAccess.updateGame(gameID, null, color);
    }

    public String clear() throws Exception {
        dataAccess.clearGameData();
        dataAccess.clearAuthData();
        dataAccess.clearUserData();

        return null;
    }

    // Utility Methods to Reduce Duplications
    private void validateAuth(String authToken) throws ServiceException, DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }
    }


    private void validateGameID(String authToken, int gameID) throws Exception {
        if (gameID > listGames(authToken).get("games").length) {
            throw new ServiceException(401, "Error: Invalid gameID");
        }
    }
}