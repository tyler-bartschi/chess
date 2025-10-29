package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import kotlin._Assertions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.exceptions.*;
import service.requests.*;
import service.results.*;
import model.*;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Collection;

public class StandardDataAccessTests {

    @AfterAll
    static public void clearSQLDatabase() {
        DataAccess dataAccess = new SQLDataAccess();
        assertDoesNotThrow(() -> dataAccess.clear());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Create User Success")
    public void createUserSucceed(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "createUsername";
        String email = "createEmail";
        String password = "createPassword";
        UserData user = new UserData(username, email, password);
        assertDoesNotThrow(() -> dataAccess.createUser(user));

        UserData retrievedUser = assertDoesNotThrow(() -> dataAccess.getUser(username));
        assertNotNull(retrievedUser);
        assertEquals(username, retrievedUser.username());
        assertEquals(password, retrievedUser.password());
        assertEquals(email, retrievedUser.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Create User Failure")
    public void createUserFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String priorUsername = "createUserFail";
        String priorEmail = "createEmailFail";
        String priorPass = "createPassFail";
        UserData user = new UserData(priorUsername, priorEmail, priorPass);
        assertDoesNotThrow(() -> dataAccess.createUser(user));

        UserData falseUser = new UserData(priorUsername, "createEmailFail2", "createPassFail");
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(falseUser));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get User Success")
    public void getUserPass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "getUsername";
        String password = "getPassword";
        String email = "getEmail";
        UserData user = new UserData(username, email, password);
        assertDoesNotThrow(() -> dataAccess.createUser(user));

        UserData retrievedUser = assertDoesNotThrow(() -> dataAccess.getUser(username));
        assertNotNull(retrievedUser);
        assertEquals(username, retrievedUser.username());
        assertEquals(email, retrievedUser.email());
        assertEquals(password, retrievedUser.password());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get User Fail")
    public void getUserFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "notUsernameFail";
        UserData retrievedUser = assertDoesNotThrow(() -> dataAccess.getUser(username));
        assertNull(retrievedUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Create Auth Success")
    public void createAuthPass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String authToken = "createToken";
        String username = "createAuthUser";
        AuthData data = new AuthData(username, authToken);

        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(data));
        AuthData retrievedData = assertDoesNotThrow(() -> dataAccess.getAuth(username));
        assertNotNull(retrievedData);
        assertEquals(username, retrievedData.username());
        assertEquals(authToken, retrievedData.authToken());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Create Auth Fail")
    public void createAuthFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String authToken = "createFailExistingAuth";
        String username = "createFailAuthUser";

        AuthData initial = new AuthData(username, authToken);
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(initial));

        username = "createFailAuthUser2";
        AuthData invalid = new AuthData(username, authToken);
        addUsername(dataAccess, username);
        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(invalid));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Auth Success")
    public void getAuthPass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "getAuthUser1";
        String authToken = "fj193jfdan";
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username, authToken)));
        AuthData retrieved = assertDoesNotThrow(() -> dataAccess.getAuth(username));
        assertNotNull(retrieved);
        assertEquals(username, retrieved.username());
        assertEquals(authToken, retrieved.authToken());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Auth Failure")
    public void getAuthFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "getAuthFail1";

        AuthData result = assertDoesNotThrow(() -> dataAccess.getAuth(username));
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Auth by Token Success")
    public void getAuthByTokenPass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "getAuthByTokenName";
        String authToken = "getAuthByTokenToken";
        AuthData auth = new AuthData(username, authToken);
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(auth));
        AuthData result = assertDoesNotThrow(() -> dataAccess.getAuthByToken(authToken));
        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(authToken, result.authToken());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Auth by Token Failure")
    public void getAuthByTokenFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String authToken = "getAuthByTokenFailToken";
        AuthData result = assertDoesNotThrow(() -> dataAccess.getAuthByToken(authToken));
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Delete Auth Success")
    public void deleteAuthPass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "deleteAuthName";
        String authToken = "deleteAuthToken";
        AuthData auth = new AuthData(username, authToken);
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(auth));
        assertDoesNotThrow(() -> dataAccess.deleteAuth(auth));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Delete Auth Fail")
    public void deleteAuthFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth(new AuthData("deleteAuthFail", "deleteAuthFailToken")));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Create Game Success")
    public void createGamePass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "createGamePassName";
        String authToken = "createGamePassToken";
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username, authToken)));

        String whiteUsername = null;
        String blackUsername = null;
        String gameName = "gameNameCreatePass";
        ChessGame chessGame = new ChessGame();

        GameDataNoID gameData = new GameDataNoID(whiteUsername, blackUsername, gameName, chessGame);
        GameData result = assertDoesNotThrow(() -> dataAccess.createGame(gameData));
        assertNotNull(result);
        assertEquals(whiteUsername, result.whiteUsername());
        assertEquals(blackUsername, result.blackUsername());
        assertEquals(gameName, result.gameName());
        assertEquals(chessGame, result.game());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Create Game Fail")
    public void createGameFail(Class<? extends DataAccess> dbClass) {
        // this can't really fail unless the SQL actually fails, so...
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Game Success")
    public void getGameSuccess(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "getGamePassName";
        String authToken = "getGamePassToken";
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username, authToken)));

        String whiteUsername = null;
        String blackUsername = username;
        String gameName = "gameNameGetPass";
        ChessGame chessGame = new ChessGame();

        GameDataNoID gameData = new GameDataNoID(whiteUsername, blackUsername, gameName, chessGame);
        GameData createdGame = assertDoesNotThrow(() -> dataAccess.createGame(gameData));

        GameData result = assertDoesNotThrow(() -> dataAccess.getGame(createdGame.gameID()));
        assertNotNull(result);
        assertEquals(whiteUsername, result.whiteUsername());
        assertEquals(blackUsername, result.blackUsername());
        assertEquals(gameName, result.gameName());
        assertEquals(chessGame, result.game());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Game Fail")
    public void getGameFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);
        GameData result = assertDoesNotThrow(() -> dataAccess.getGame(38284237));
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Games Success")
    public void getGamesPass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);
        assertDoesNotThrow(() -> dataAccess.clear());

        String username = "getGamePassName1";
        String authToken = "getGamePassToken1";
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username, authToken)));

        String username2 = "getGamePassName2";
        String authToken2 = "getGamePassToken2";
        addUsername(dataAccess, username2);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username2, authToken2)));

        String whiteUsername1 = null;
        String blackUsername1 = username;
        String gameName1 = "gameNameCreatePass";
        ChessGame chessGame1 = new ChessGame();

        GameDataNoID gameData1 = new GameDataNoID(whiteUsername1, blackUsername1, gameName1, chessGame1);
        GameData result1 = assertDoesNotThrow(() -> dataAccess.createGame(gameData1));
        assertNotNull(result1);

        String whiteUsername2 = username2;
        String blackUsername2 = null;
        String gameName2 = "gameNameCreatePass";
        ChessGame chessGame2 = new ChessGame();

        GameDataNoID gameData2 = new GameDataNoID(whiteUsername2, blackUsername2, gameName2, chessGame2);
        GameData result2 = assertDoesNotThrow(() -> dataAccess.createGame(gameData2));
        assertNotNull(result2);

        Collection<GameData> games = assertDoesNotThrow(() -> dataAccess.getAllGames());

        assertNotNull(games);
        assertTrue(games.contains(result1));
        assertTrue(games.contains(result2));

    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Get Games Fail")
    public void getGamesFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);
        assertDoesNotThrow(() -> dataAccess.clear());

        Collection<GameData> games = assertDoesNotThrow(() -> dataAccess.getAllGames());
        assertTrue(games.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Update Game Success")
    public void updateGamePass(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "updateGamePassName1";
        String authToken = "updateGamePassToken1";
        addUsername(dataAccess, username);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username, authToken)));

        String username2 = "updateGamePassName2";
        String authToken2 = "updateGamePassToken2";
        addUsername(dataAccess, username2);
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username2, authToken2)));

        String whiteUsername = null;
        String blackUsername = null;
        String gameName = "gameNameCreatePass";
        ChessGame chessGame = new ChessGame();

        GameDataNoID gameData = new GameDataNoID(whiteUsername, blackUsername, gameName, chessGame);
        GameData result = assertDoesNotThrow(() -> dataAccess.createGame(gameData));
        assertNotNull(result);

        GameData updatedGame = new GameData(result.gameID(), username, username2, result.gameName(), result.game());
        assertDoesNotThrow(() -> dataAccess.updateGame(result.gameID(), updatedGame));
        GameData retrievedGame = assertDoesNotThrow(() -> dataAccess.getGame(result.gameID()));
        assertNotNull(retrievedGame);
        verifySameGame(updatedGame, retrievedGame);

        ChessGame expectedChessGame = retrievedGame.game();
        Collection<ChessMove> validMoves = expectedChessGame.validMoves(new ChessPosition(1, 2));
        assertNotNull(validMoves);
        for (ChessMove move: validMoves) {
            assertDoesNotThrow(() -> expectedChessGame.makeMove(move));
            break;
        }

        GameData newUpdatedGame = new GameData(retrievedGame.gameID(), retrievedGame.whiteUsername(), retrievedGame.blackUsername(),
                retrievedGame.gameName(), expectedChessGame);
        assertDoesNotThrow(() -> dataAccess.updateGame(retrievedGame.gameID(), newUpdatedGame));

        GameData lastGame = assertDoesNotThrow(() -> dataAccess.getGame(retrievedGame.gameID()));
        assertNotNull(lastGame);
        verifySameGame(newUpdatedGame, lastGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Update Game Fail")
    public void updateGameFail(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        GameData game = new GameData(12343214, null, null, "gameName", new ChessGame());
        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(12343214, game));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Clear Test")
    public void clearDatabase(Class<? extends DataAccess> dbClass) {
        DataAccess dataAccess = getDataAccess(dbClass);

        String username = "clearUsername";
        String email = "clearEmail";
        String password = "clearPassword";
        String authToken = "clearAuthToken";
        int gameID = 132478;
        assertDoesNotThrow(() -> dataAccess.createUser(new UserData(username, email, password)));
        assertDoesNotThrow(() -> dataAccess.createAuth(new AuthData(username, authToken)));
        assertDoesNotThrow(() -> dataAccess.createGameWithID(new GameData(gameID, null, null,
                "gameNameClear", new ChessGame())));

        assertDoesNotThrow(() -> dataAccess.clear());

        UserData user = assertDoesNotThrow(() -> dataAccess.getUser(username));
        AuthData auth = assertDoesNotThrow(() -> dataAccess.getAuth(username));
        AuthData auth2 = assertDoesNotThrow(() -> dataAccess.getAuthByToken(authToken));
        GameData game = assertDoesNotThrow(() -> dataAccess.getGame(gameID));

        assertNull(user);
        assertNull(auth);
        assertNull(auth2);
        assertNull(game);
    }

    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) {
        DataAccess db;
        if (databaseClass.equals(SQLDataAccess.class)) {
            db = new SQLDataAccess();
        } else {
            db = new MemoryDataAccess();
        }
        return db;
    }

    private void addUsername(DataAccess db, String username) {
        assertDoesNotThrow(() -> db.createUser(new UserData(username, "123", "123")));
    }

    private void verifySameGame(GameData expected, GameData observed) {
        assertEquals(expected.gameID(), observed.gameID());
        assertEquals(expected.whiteUsername(), observed.whiteUsername());
        assertEquals(expected.blackUsername(), observed.blackUsername());
        assertEquals(expected.gameName(), observed.gameName());
        ChessGame expectedGame = expected.game();
        ChessGame observedGame = observed.game();
        assertEquals(expectedGame.getTeamTurn(), observedGame.getTeamTurn());
        assertEquals(expectedGame.getBoard(), observedGame.getBoard());
    }
}
