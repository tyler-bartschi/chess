package service;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.*;
import server.exceptions.*;
import service.requests.*;
import service.results.*;
import model.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StandardServiceTests {

    private final static String START_USERNAME = "existingUser";
    private final static String START_EMAIL = "existingEmail";
    private final static String START_PASSWORD = "existingPassword";

    private static DataAccess testDataAccess;
    private static UserService testUserService;

    @BeforeAll
    public static void init() {
        testDataAccess = new MemoryDataAccess();
        testUserService = new UserService(testDataAccess);
        RegisterRequest request = new RegisterRequest(START_USERNAME, START_EMAIL, START_PASSWORD);
        assertDoesNotThrow(() -> testUserService.register(request), "initial register in initialization fails");
    }


    @Test
    @Order(1)
    @DisplayName("Register Successful")
    public void registerSuccess() {
        RegisterRequest request = new RegisterRequest("myUser", "myEmail", "123");
        RegisterResult result = assertDoesNotThrow(() -> testUserService.register(request), "Initial register failed");
        assertNotNull(result, "Result was null");
        assertInstanceOf(String.class, result.authToken(), "Result did not contain a valid authToken");
        assertFalse(result.authToken().isEmpty(), "authToken is empty");
        assertEquals(request.username(), result.username(), "Username of result does not match username of request");
    }

    @Test
    @Order(2)
    @DisplayName("Register Bad Request")
    public void registerBadReq() {
        RegisterRequest request = new RegisterRequest(START_USERNAME, "hasEmail", "2");
        assertThrows(AlreadyTakenException.class, () -> testUserService.register(request), "Does not throw a bad request exception");
    }

    @Test
    @Order(3)
    @DisplayName("Login Successful")
    public void loginSuccess() {
        LoginRequest request = new LoginRequest(START_USERNAME, START_PASSWORD);
        LoginResult result = assertDoesNotThrow(() -> testUserService.login(request), "Initial login failed");
        assertNotNull(result, "Result was null");
        assertInstanceOf(String.class, result.authToken(), "Result did not contain a valid authToken");
        assertFalse(result.authToken().isEmpty(), "authToken is empty");
        assertEquals(request.username(), result.username(), "Username of result does not match username of request");

    }

    @Test
    @Order(4)
    @DisplayName("Login Bad Password")
    public void loginBadPass() {
        LoginRequest request = new LoginRequest(START_USERNAME, "notPassword");
        assertThrows(UnauthorizedException.class, () -> testUserService.login(request), "Does not throw an unauthorized exception for invalid password");

    }

    @Test
    @Order(5)
    @DisplayName("Login Invalid Username")
    public void loginBadUser() {
        LoginRequest request = new LoginRequest("nonExistentUser", "123");
        assertThrows(UnauthorizedException.class, () -> testUserService.login(request), "Does not throw an unauthorized exception for invalid username");
    }

    @Test
    @Order(6)
    @DisplayName("Clear Successful")
    public void clearSuccess() {
        testUserService.clear();
        UserData noUser = testDataAccess.getUser(START_USERNAME);
        AuthData noAuth = testDataAccess.getAuth(START_USERNAME);
        assertNull(noUser, "Did not clear the user datatable");
        assertNull(noAuth, "Did not clear the auth datatable");
    }
}
