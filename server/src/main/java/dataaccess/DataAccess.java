package dataaccess;

import model.*;

public interface DataAccess {
    UserData getUser(String username);

    UserData createUser(UserData userData);

    AuthData createAuth(AuthData authData);

    AuthData getAuth(String authToken);
}
