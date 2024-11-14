package client;

import chess.ChessGame;
import dataaccess.MySqlDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static MySqlDataAccess dataAccess;

    @BeforeAll
    public static void init() {
        dataAccess = new MySqlDataAccess();
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + String.valueOf(port));
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterEach
    void clearDatabase() throws Exception {
        dataAccess.clearGameData();
        dataAccess.clearAuthData();
        dataAccess.clearUserData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void createUser() throws Exception {
        UserData user = new UserData("username", "password", "email");

        var result = facade.createUser(user);
        Assertions.assertTrue(result.length() > 20);
    }

    @Test
    public void createUserNoPassword() {
        UserData user = new UserData("username2", null, "email");
        Assertions.assertThrows(Exception.class, () -> {
            facade.createUser(user);
        });
    }

    @Test
    public void loginUser() throws Exception {
        UserData user = new UserData("username", "password", "email");
        facade.createUser(user);

        var result = facade.loginUser(user);
        Assertions.assertTrue(result.length() > 20);
    }

    @Test
    public void userDoesNotExist() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.loginUser(new UserData("username", "password", null));
        });
    }

    @Test
    public void wrongPassword() throws Exception {
        UserData user = new UserData("username", "password", "email");
        facade.createUser(user);

        Assertions.assertThrows(Exception.class, () -> {
            facade.loginUser(new UserData("username", "wrong", null));
        });
    }

    @Test
    public void createGame() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        Assertions.assertDoesNotThrow(() -> {
            facade.createGame(authToken, "gameName");
        });
    }

    @Test
    public void createGameNoName() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(authToken, null);
        });
    }

    @Test
    public void listGamesNoGames() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        var result = facade.listGames(authToken);
        Assertions.assertEquals("No games! Do 'create <NAME>' to create one!", result);
    }

    private void createGames(String authToken) throws Exception {
        facade.createGame(authToken, "gameName");
        facade.createGame(authToken, "gameName2");
        facade.createGame(authToken, "gameName3");
        facade.createGame(authToken, "gameName4");
    }

    @Test
    public void listGames() throws Exception {
        String expected = "1. gameName\n" +
                "    White: No Player\n" +
                "    Black: No Player\n" +
                "2. gameName2\n" +
                "    White: No Player\n" +
                "    Black: No Player\n" +
                "3. gameName3\n" +
                "    White: No Player\n" +
                "    Black: No Player\n" +
                "4. gameName4\n" +
                "    White: No Player\n" +
                "    Black: No Player";

        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);

        var result = facade.listGames(authToken);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void joinGame() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);

        Assertions.assertDoesNotThrow(() -> {
            var result = facade.joinGame(authToken, "1", "white");
            Assertions.assertEquals(ChessGame.class, result.getClass());
        });
    }

    @Test
    public void joinGameWrongId() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(authToken, "20", "white");
        });
    }

    @Test
    public void joinGameInvalidColor() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(authToken, "1", "blue");
        });
    }

    @Test
    public void joinGameColorInUse() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);
        facade.joinGame(authToken, "1", "white");

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(authToken, "1", "white");
        });
    }

    @Test
    public void observeGame() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);

        Assertions.assertDoesNotThrow(() -> {
            var result = facade.observeGame(authToken, "1");
            Assertions.assertEquals(ChessGame.class, result.getClass());
        });
    }

    @Test
    public void observeGameWrongId() throws Exception {
        UserData user = new UserData("username", "password", "email");
        String authToken = facade.createUser(user);

        createGames(authToken);

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(authToken, "20", "white");
        });
    }
}
