package dataaccess;

import model.*;

public interface DataAccess {
    UserData getUser(String username) throws Exception;

    UserData createUser(UserData userData) throws DataAccessException;

    AuthData createAuth(AuthData authData) throws Exception;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    boolean checkColorUsername(int gameID, String color) throws DataAccessException;

    void updateGame(int gameID, String username, String color) throws DataAccessException;

    void clearGameData() throws DataAccessException;

    void clearAuthData() throws DataAccessException;

    void clearUserData() throws DataAccessException;
}
