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
    private final static String START_AUTH = "auth123";

    private static DataAccess testDataAccess;
    private static UserService testUserService;
    private static GameService testGameService;

    @BeforeAll
    public static void init() {
        testDataAccess = new MemoryDataAccess();
        testUserService = new UserService(testDataAccess);
        testGameService = new GameService(testDataAccess);
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
    @DisplayName("Logout Successful")
    public void logoutSuccess() {
        testDataAccess.createAuth(new AuthData("username", START_AUTH));
        LogoutRequest request = new LogoutRequest(START_AUTH);
        SuccessEmptyResult result = assertDoesNotThrow(() -> testUserService.logout(request), "initial logout failed");
        assertNotNull(result, "Result was null");
        assertInstanceOf(SuccessEmptyResult.class, result, "Result was of improper type");
    }

    @Test
    @Order(7)
    @DisplayName("Logout Failed Bad Auth")
    public void logoutBadAuth() {
        testDataAccess.createAuth(new AuthData("username", START_AUTH));
        LogoutRequest request = new LogoutRequest("definitelyNotAnAuthToken");
        assertThrows(UnauthorizedException.class, () -> testUserService.logout(request));
        testUserService.logout(new LogoutRequest(START_AUTH));
    }

    @Test
    @Order(8)
    @DisplayName("Logout Tried Multiple Times")
    public void logoutMultiple() {
        testDataAccess.createAuth(new AuthData("username", START_AUTH));
        LogoutRequest req = new LogoutRequest(START_AUTH);
        assertDoesNotThrow(() -> testUserService.logout(req), "initial logout failed");
        assertThrows(UnauthorizedException.class, () -> testUserService.logout(req), "second logout was successful");
    }

    @Test
    @Order(9)
    @DisplayName("Create Game Successful")
    public void createSuccess() {
        testDataAccess.createAuth(new AuthData("username", START_AUTH));
        CreateRequest req = new CreateRequest(START_AUTH, "nameOfGame");
        CreateResult res = assertDoesNotThrow(() -> testGameService.createGame(req), "Failed to create game");
        assertInstanceOf(Integer.class, res.gameID(), "gameID is not an integer");
        GameData obj = testDataAccess.getGame(res.gameID());
        assertNotNull(obj, "GameData object is null");
        assertEquals(req.gameName(), obj.gameName(), "Game name does not match");
    }

    @Test
    @Order(10)
    @DisplayName("Create Game Auth Failure")
    public void createFail() {
        assertThrows(UnauthorizedException.class, () -> testGameService.createGame(new CreateRequest("definitelyNotYourAuth", "name")));
    }

    @Test
    @Order(11)
    @DisplayName("Clear Successful")
    public void clearSuccess() {
        testDataAccess.createAuth(new AuthData("username", START_AUTH));
        testUserService.clear();
        UserData noUser = testDataAccess.getUser(START_USERNAME);
        AuthData noAuth = testDataAccess.getAuth(START_USERNAME);
        AuthData stillNoAuth = testDataAccess.getAuthByToken(START_AUTH);
        assertNull(noUser, "Did not clear the user datatable");
        assertNull(noAuth, "Did not clear the authByUsername datatable");
        assertNull(stillNoAuth, "Did not clear the authByToken datatable");
    }
}
