package service;

import dataaccess.DataAccess;

public class LoginService {
    private final DataAccess dataAccess;

    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
