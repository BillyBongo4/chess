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
}
