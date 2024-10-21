package dataaccess;

import model.*;

public interface DataAccess {
    UserData getUser(String username);

    UserData createUser(UserData userData);

    AuthData createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    GameData[] listGames();

    GameData createGame(GameData gameData);

    GameData getGame(int gameID);

    boolean checkColorUsername(int gameID, String color) throws DataAccessException;

    void updateGame(int gameID, String username, String color);

    void clearGameData();

    void clearAuthData();

    void clearUserData();
}
