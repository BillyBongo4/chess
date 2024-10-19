package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();

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
        auths.put(authData.username(), authData);
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
}
