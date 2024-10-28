package dataaccess;

import model.*;

public class MySqlDataAccess implements DataAccess {
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public UserData createUser(UserData userData) {
        return null;
    }

    @Override
    public AuthData createAuth(AuthData authData) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public GameData[] listGames() {
        return new GameData[0];
    }

    @Override
    public GameData createGame(GameData gameData) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public boolean checkColorUsername(int gameID, String color) throws DataAccessException {
        return false;
    }

    @Override
    public void updateGame(int gameID, String username, String color) {

    }

    @Override
    public void clearGameData() {

    }

    @Override
    public void clearAuthData() {

    }

    @Override
    public void clearUserData() {

    }
}
