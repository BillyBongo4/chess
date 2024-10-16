package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

public class LoginService {
    private final DataAccess dataAccess;

    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData getUser(String username) {
        return dataAccess.getUser(username);
    }

    public AuthData createAuth(AuthData authData) {
        return dataAccess.createAuth(authData);
    }
}
