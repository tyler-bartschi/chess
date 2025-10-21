package service;

import chess.ChessGame;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.*;
import server.exceptions.*;
import service.requests.*;
import service.results.*;
import model.*;

import java.util.Collection;

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
        assertThrows(UnauthorizedException.class, () -> testUserService.login(request),
                "Does not throw an unauthorized exception for invalid password");

    }

    @Test
    @Order(5)
    @DisplayName("Login Invalid Username")
    public void loginBadUser() {
        LoginRequest request = new LoginRequest("nonExistentUser", "123");
        assertThrows(UnauthorizedException.class, () -> testUserService.login(request),
                "Does not throw an unauthorized exception for invalid username");
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
    @DisplayName("Join Game Success")
    public void joinSuccess() {
        String authToken = "authDJ2";
        String user1 = "joinUser";
        String user2 = "twoUser";
        testDataAccess.createAuth(new AuthData(user1, START_AUTH));
        testDataAccess.createAuth(new AuthData(user2, authToken));
        CreateResult cRes = testGameService.createGame(new CreateRequest(START_AUTH, "joinGameSuccess"));
        int gameID = cRes.gameID();

        JoinRequest req1 = new JoinRequest(START_AUTH, "WHITE", gameID);
        JoinRequest req2 = new JoinRequest(authToken, "BLACK", gameID);

        assertDoesNotThrow(() -> testGameService.joinGame(req1));
        assertDoesNotThrow(() -> testGameService.joinGame(req2));

        GameData game = testDataAccess.getGame(gameID);
        assertEquals(user1, game.whiteUsername(), "White username does not match");
        assertEquals(user2, game.blackUsername(), "Black username does not match");
    }

    @Test
    @Order(12)
    @DisplayName("Join Game Bad Request")
    public void joinBadReq() {
        testDataAccess.createAuth(new AuthData("myUser", START_AUTH));
        CreateResult cRes = testGameService.createGame(new CreateRequest(START_AUTH, "joinGameBad"));
        int gameID = cRes.gameID();

        JoinRequest req = new JoinRequest(START_AUTH, "WHITE", -2314781);
        assertThrows(InvalidRequestException.class, () -> testGameService.joinGame(req), "Invalid request went through");
    }

    @Test
    @Order(13)
    @DisplayName("Join Game Unauthorized")
    public void joinUnauthorized() {
        testDataAccess.createAuth(new AuthData("myUser", START_AUTH));
        CreateResult cRes = testGameService.createGame(new CreateRequest(START_AUTH, "joinGameBad"));
        int gameID = cRes.gameID();

        JoinRequest req = new JoinRequest("I'm Unauthorized!", "BLACK", gameID);
        assertThrows(UnauthorizedException.class, () -> testGameService.joinGame(req), "Unauthorized user was able to join game");
    }

    @Test
    @Order(14)
    @DisplayName("Join Game Already Taken")
    public void joinAlreadyTaken() {
        String authToken = "authDJ2";
        String user1 = "joinUser";
        String user2 = "twoUser";
        testDataAccess.createAuth(new AuthData(user1, START_AUTH));
        testDataAccess.createAuth(new AuthData(user2, authToken));
        CreateResult cRes = testGameService.createGame(new CreateRequest(START_AUTH, "joinGameSuccess"));
        int gameID = cRes.gameID();

        JoinRequest req1 = new JoinRequest(START_AUTH, "WHITE", gameID);
        JoinRequest req2 = new JoinRequest(authToken, "WHITE", gameID);

        assertDoesNotThrow(() -> testGameService.joinGame(req1));
        assertThrows(AlreadyTakenException.class, () -> testGameService.joinGame(req2), "Second user was able to join as the same color");
    }

    @Test
    @Order(15)
    @DisplayName("List Request Success")
    public void listSuccess() {
        testUserService.clear();
        int gameID1 = 1234234;
        String gameName1 = "FirstGame";
        int gameID2 = 123414321;
        String whiteUsername = "nomnom";
        String gameName2 = "SecondGame";
        testDataAccess.createAuth(new AuthData("thisWorks", START_AUTH));
        testDataAccess.createGame(new GameData(gameID1, null, null, gameName1, new ChessGame()));
        testDataAccess.createGame(new GameData(gameID2, whiteUsername, null, gameName2, new ChessGame()));

        ListResult res = assertDoesNotThrow(() -> testGameService.listGames(new ListRequest(START_AUTH)));
        assertNotNull(res);
        Collection<AbbrGameData> games = res.games();
        AbbrGameData firstGame = new AbbrGameData(gameID1, null, null, gameName1);
        AbbrGameData secondGame = new AbbrGameData(gameID2, whiteUsername, null, gameName2);
        assertTrue(games.contains(firstGame));
        assertTrue(games.contains(secondGame));

        AbbrGameData falseGame = new AbbrGameData(123412, "hello", "goodbye", "notAName");
        assertFalse(games.contains(falseGame));
    }

    @Test
    @Order(16)
    @DisplayName("List Request Bad Auth")
    public void listFail() {
        testDataAccess.createAuth(new AuthData("listFail", START_AUTH));
        assertThrows(UnauthorizedException.class, () -> testGameService.listGames(new ListRequest("Definitely not an authToken")));
    }

    @Test
    @Order(17)
    @DisplayName("Clear Successful")
    public void clearSuccess() {
        testDataAccess.createAuth(new AuthData("username", START_AUTH));
        testDataAccess.createGame(new GameData(1234, null, null, "testGame", new ChessGame()));
        testUserService.clear();
        UserData noUser = testDataAccess.getUser(START_USERNAME);
        AuthData noAuth = testDataAccess.getAuth(START_USERNAME);
        AuthData stillNoAuth = testDataAccess.getAuthByToken(START_AUTH);
        GameData noGame = testDataAccess.getGame(1234);
        assertNull(noUser, "Did not clear the user datatable");
        assertNull(noAuth, "Did not clear the authByUsername datatable");
        assertNull(stillNoAuth, "Did not clear the authByToken datatable");
        assertNull(noGame, "Did not clear the games datatable");
    }
}
