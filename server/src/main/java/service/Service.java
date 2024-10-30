package service;

import chess.ChessGame;
import dataaccess.DataAccess;
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
        String test = dataAccess.getUser(user.username()).password();
        if (dataAccess.getUser(user.username()) == null) {
            throw new ServiceException(401, "Error: User doesn't exists");
        } else if (BCrypt.checkpw(user.username(), dataAccess.getUser(user.username()).password())) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        return createAuth(user);
    }

    public String logoutUser(String authToken) throws Exception {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        dataAccess.deleteAuth(authToken);

        return null;
    }

    public Map<String, GameListEntry[]> listGames(String authToken) throws Exception {
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

    public Map<String, Integer> createGame(String authToken, String gameName) throws Exception {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        var gameData = dataAccess.createGame(gameName);

        Map<String, Integer> result = new HashMap<>();
        result.put("gameID", gameData.gameId());
        return result;
    }

    public String joinGame(String authToken, int gameID, String playerColor) throws Exception {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }

        if (dataAccess.checkColorUsername(gameID, playerColor)) {
            throw new ServiceException(403, "Error: Already taken");
        }


        UserData user = dataAccess.getUser(authData.username());
        validateUserData(user);

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
