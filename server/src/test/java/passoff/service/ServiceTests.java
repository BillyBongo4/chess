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

    @BeforeAll
    public static void init() {
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);
    }

    @Test
    public void registerUser() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        var auth = new AuthData(UUID.randomUUID().toString(), "a");
        var registrationResult = service.registerUser(user);
        Assertions.assertEquals(auth.username(), registrationResult.username());
    }

    @Test
    public void userAlreadyExists() throws Exception {
        var user = new UserData("a", "p", "a@a.com");
        service.registerUser(user);

        assertThrows(ServiceException.class, () -> {
            service.registerUser(user);
        });
    }
}
