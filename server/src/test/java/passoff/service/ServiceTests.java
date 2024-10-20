package passoff.service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
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
}
