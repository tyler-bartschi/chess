package service;

import org.junit.jupiter.api.*;
import dataaccess.*;
import server.exceptions.*;
import service.requests.*;
import service.results.*;

public class StandardServiceTests {

    private static DataAccess testDataAccess;
    private static UserService testUserService;

    @BeforeAll
    public static void init() {
        testDataAccess = new MemoryDataAccess();
        testUserService = new UserService(testDataAccess);
        RegisterRequest request = new RegisterRequest("existingUser", "existingEmail", "123");
        testUserService.register(request);
    }

    @Test
    @DisplayName("Register Successful")
    public void registerSuccess() {
        RegisterRequest request = new RegisterRequest("myUser", "myEmail", "123");
        RegisterResult result = testUserService.register(request);
        Assertions.assertNotNull(result, "Result was null");
        Assertions.assertNotNull(result.authToken(), "Result did not contain and AuthToken");
        Assertions.assertEquals(request.username(), result.username(), "Username of result does not match username of request");
    }

    @Test
    @DisplayName("Register Bad Request")
    public void registerBadReq() {
        RegisterRequest request = new RegisterRequest("existingUser", "hasEmail", "2");
        Assertions.assertThrows(AlreadyTakenException.class, () -> testUserService.register(request), "Does not throw a bad request exception");
    }
}
