package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.UUID;

public class Service {
    DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private AuthData createAuth(UserData user) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("User already exists");
        }

        dataAccess.createUser(newUser);

        return createAuth(newUser);
    }

    public AuthData loginUser(UserData user) throws ServiceException {
        if (dataAccess.getUser(user.username()) == null) {
            throw new ServiceException("User doesn't exists");
        }

        return createAuth(user);
    }

    public String logoutUser(String authToken) throws ServiceException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException("Unauthorized");
        }

        dataAccess.deleteAuth(authToken);

        return "";
    }

    public GameData[] listGames(String authToken) throws ServiceException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException("Unauthorized");
        }

        return dataAccess.listGames();
    }

    public int createGame(String authToken, String gameName) throws ServiceException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException("Unauthorized");
        }

        int gameID = listGames(authToken).length + 1;
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, game);

        return dataAccess.createGame(gameData).gameId();
    }

    public String joinGame(String authToken, UserData user, int gameID, String playerColor) throws Exception {
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServiceException("Unauthorized");
        }

        if (dataAccess.checkColorUsername(gameID, playerColor)) {
            throw new ServiceException("Already taken");
        }

        dataAccess.updateGame(gameID, user.username(), playerColor);

        return "";
    }

    public String clear() throws ServiceException {
        dataAccess.clearGameData();
        dataAccess.clearAuthData();
        dataAccess.clearUserData();
        
        return "";
    }
}
