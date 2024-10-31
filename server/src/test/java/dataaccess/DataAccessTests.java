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

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getAuthWhenThereIsNone(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var result = dataAccess.getAuth("test");

        assertEquals(null, result);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = new GameData(1, null, null, "testGame", new ChessGame());
        var result = dataAccess.createGame("testGame");

        assertEquals(game.gameId(), result.gameId());
        assertEquals(game.whiteUsername(), result.whiteUsername());
        assertEquals(game.blackUsername(), result.blackUsername());
        assertEquals(game.gameName(), result.gameName());
        assertNotNull(result.game());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createGameWithNoName(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(null);
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listGames(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);


        dataAccess.createGame("testGame1");
        dataAccess.createGame("testGame2");
        dataAccess.createGame("testGame3");
        dataAccess.createGame("testGame4");

        var result = dataAccess.listGames();

        assertEquals(4, result.length);
        assertEquals("testGame1", result[0].gameName());
        assertEquals("testGame2", result[1].gameName());
        assertEquals("testGame3", result[2].gameName());
        assertEquals("testGame4", result[3].gameName());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void noGames(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        var result = dataAccess.listGames();

        assertEquals(0, result.length);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void updateGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.createGame("testGame");

        assertDoesNotThrow(() -> {
            dataAccess.updateGame(1, "testUsername", "WHITE");
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void updateGameInvalid(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.createGame("testGame");

        assertThrows(DataAccessException.class, () -> {
            dataAccess.updateGame(999999, "", "WHITE");
            dataAccess.updateGame(1, "", "WHITE");
            dataAccess.updateGame(1, "testUsername", "BLUE");
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void checkColorUsername(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.createGame("testGame");

        var result1 = dataAccess.checkColorUsername(1, "WHITE");
        assertFalse(result1);

        dataAccess.updateGame(1, "testUsername", "WHITE");

        var result2 = dataAccess.checkColorUsername(1, "WHITE");
        assertTrue(result2);


        var result3 = dataAccess.checkColorUsername(1, "BLACK");
        assertFalse(result3);

        dataAccess.updateGame(1, "testUsername", "BLACK");

        var result4 = dataAccess.checkColorUsername(1, "BLACK");
        assertTrue(result4);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void clear(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        assertDoesNotThrow(() -> {
            dataAccess.clearGameData();
            dataAccess.clearAuthData();
            dataAccess.clearUserData();
        });
    }
}

