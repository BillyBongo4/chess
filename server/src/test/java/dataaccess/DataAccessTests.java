package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.Server;
import model.UserData;
import org.junit.jupiter.api.*;
import service.Service;
import service.ServiceException;

import javax.sql.rowset.serial.SerialException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataAccessTests {
    private static DataAccess dataAccess;

    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess db;
        if (databaseClass.equals(MySqlDataAccess.class)) {
            db = new MySqlDataAccess();
        } else {
            db = new MemoryDataAccess();
        }
        db.clearGameData();
        db.clearAuthData();
        db.clearUserData();
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var user = new UserData("testUsername", "testPassword", "test@email.com");
        var result = dataAccess.createUser(user);

        assertEquals(user, result);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUserWithoutPassword(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var user = new UserData("testUsername", null, "test@email.com");

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user);
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var user = new UserData("testUsername", "testPassword", "test@email.com");
        dataAccess.createUser(user);

        var result = dataAccess.getUser("testUsername");

        assertEquals(user, result);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getUserWhenUserDoesNotExist(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var result = dataAccess.getUser("testUsername");

        assertEquals(null, result);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var authData = new AuthData(UUID.randomUUID().toString(), "testUsername");
        var result = dataAccess.createAuth(authData);

        assertEquals(authData, result);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createAuthNoAuthToken(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var authData = new AuthData(null, "testUsername");

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createAuth(authData);
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var authData = new AuthData(UUID.randomUUID().toString(), "testUsername");
        dataAccess.createAuth(authData);

        var result = dataAccess.getAuth(authData.authToken());

        assertEquals(authData, result);
    }
}

