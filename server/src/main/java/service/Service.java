package service;

import dataaccess.DataAccess;
import model.UserData;

public class Service {
    DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData registerUser(UserData newUser) throws ServiceException {
        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("User already exists");
        }

        return newUser;
    }
}
