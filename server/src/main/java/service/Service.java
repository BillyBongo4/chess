package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;

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
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        validateUserData(newUser);
        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException(403, "Error: User already exists");
        }

        dataAccess.createUser(newUser);

        return createAuth(newUser);
    }

    public AuthData loginUser(UserData user) throws ServiceException {
        validateUserData(user);
        if (dataAccess.getUser(user.username()) == null) {
            throw new ServiceException(401, "Error: User doesn't exists");
        } else if (!user.password().equals(dataAccess.getUser(user.username()).password())) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        return createAuth(user);
    }

    public String logoutUser(String authToken) throws ServiceException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        dataAccess.deleteAuth(authToken);

        return null;
    }

    public Map<String, GameListEntry[]> listGames(String authToken) throws ServiceException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

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

    public Map<String, Integer> createGame(String authToken, String gameName) throws ServiceException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }
        int gameID = listGames(authToken).get("games").length + 1;
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, game);
        dataAccess.createGame(gameData);

        Map<String, Integer> result = new HashMap<>();
        result.put("gameID", gameID);
        return result;
    }

    public String joinGame(String authToken, UserData user, int gameID, String playerColor) throws Exception {
        validateUserData(user);
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        if (dataAccess.checkColorUsername(gameID, playerColor)) {
            throw new ServiceException(403, "Error: Already taken");
        }

        dataAccess.updateGame(gameID, user.username(), playerColor);

        return null;
    }

    public String clear() {
        dataAccess.clearGameData();
        dataAccess.clearAuthData();
        dataAccess.clearUserData();

        return null;
    }
}
