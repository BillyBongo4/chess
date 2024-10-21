package passoff.service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import server.Server;
import model.UserData;
import org.junit.jupiter.api.*;
import service.Service;
import service.ServiceException;

import javax.sql.rowset.serial.SerialException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceTests {
    private static DataAccess dataAccess;
    private static Service service;

    @BeforeEach
    public void init() {
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);
    }

    @Test
    public void registerUser() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expectedAuth = new AuthData(UUID.randomUUID().toString(), "a");
        var registrationResult = service.registerUser(user);
        assertEquals(expectedAuth.username(), registrationResult.username());
    }

    @Test
    public void userAlreadyExists() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        service.registerUser(user);

        assertThrows(ServiceException.class, () -> {
            service.registerUser(user);
        });
    }

    @Test
    public void loginUser() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expectedAuth = new AuthData(UUID.randomUUID().toString(), "a");
        service.registerUser(user);

        var loginResult = service.loginUser(user);
        assertEquals(expectedAuth.username(), loginResult.username());
    }

    @Test
    public void userDoesNotExist() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expectedAuth = new AuthData(UUID.randomUUID().toString(), "a");

        assertThrows(ServiceException.class, () -> {
            service.loginUser(user);
        });
    }

    @Test
    public void logoutUser() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expected = "";

        var authData = service.registerUser(user);

        var logoutResult = service.logoutUser(authData.authToken());
        assertEquals(expected, logoutResult);
    }

    @Test
    public void noAuthorization() throws Exception {
        assertThrows(ServiceException.class, () -> {
            service.logoutUser("");
        });
    }

    @Test
    public void registerLogoutLogBackInAndThenLogOutAgain() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expected = "";

        var authData = service.registerUser(user);
        service.logoutUser(authData.authToken());
        authData = service.loginUser(user);

        var logoutResult = service.logoutUser(authData.authToken());

        assertEquals(expected, logoutResult);
    }

    @Test
    public void listGames() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expected = 3;

        var authData = service.registerUser(user);

        service.createGame(authData.authToken(), "game");
        service.createGame(authData.authToken(), "game2");
        service.createGame(authData.authToken(), "game3");

        var listGamesResult = service.listGames(authData.authToken());

        assertEquals(expected, listGamesResult.length);
    }

    @Test
    public void listGamesNoAuthorization() throws Exception {
        assertThrows(ServiceException.class, () -> {
            service.listGames("");
        });
    }

    @Test
    public void createGame() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expected = 1;

        var authData = service.registerUser(user);

        var createGameResult = service.createGame(authData.authToken(), "name");

        assertEquals(expected, createGameResult);
    }

    @Test
    public void createSecondGame() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var expected = 2;

        var authData = service.registerUser(user);

        service.createGame(authData.authToken(), "second");

        var createGameResult = service.createGame(authData.authToken(), "name");

        assertEquals(expected, createGameResult);
    }

    @Test
    public void createGameNoAuthorizatio() {
        assertThrows(ServiceException.class, () -> {
            service.createGame("", "");
        });
    }
}
