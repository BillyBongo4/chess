package service;

import dataaccess.DataAccess;
import model.*;

public class RegisterService {
    private final DataAccess dataAccess;

    public RegisterService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData getUser(String username) {
        return dataAccess.getUser(username);
    }

    public UserData createUser(UserData userData) {
        return dataAccess.createUser(userData);
    }

    public AuthData createAuth(AuthData authData) {
        return dataAccess.createAuth(authData);
    }
}
