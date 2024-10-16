package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

public class LogoutService {
    private final DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData getAuth(String authToken) {
        return dataAccess.getAuth(authToken);
    }

    public void deleteAuth(String authToken) {
        dataAccess.deleteAuth(authToken);
    }
}
