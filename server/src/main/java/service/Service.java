package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class Service {
    DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("User already exists");
        }

        dataAccess.createUser(newUser);

        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), newUser.username());
        dataAccess.createAuth(newAuth);

        return newAuth;
    }
}
