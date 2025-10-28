package dataaccess;

import chess.ChessGame;
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
import java.util.Collection;

public class StandardDataAccessTests {

    // add an after all to clear all datatables

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

    @Test
    @DisplayName("Get Auth by Token Success")
    public void getAuthByTokenPass() {

    }

    @Test
    @DisplayName("Get Auth by Token Failure")
    public void getAuthByTokenFail() {

    }

    @Test
    @DisplayName("Delete Auth Success")
    public void deleteAuthPass() {

    }

    @Test
    @DisplayName("Delete Auth Fail")
    public void deleteAuthFail() {

    }

    @Test
    @DisplayName("Create Game Success")
    public void createGamePass() {

    }

    @Test
    @DisplayName("Create Game Fail")
    public void createGameFail() {

    }

    @Test
    @DisplayName("Get Game Success")
    public void getGameSuccess() {

    }

    @Test
    @DisplayName("Get Game Fail")
    public void getGameFail() {

    }

    @Test
    @DisplayName("Get Games Success")
    public void getGamesPass() {

    }

    @Test
    @DisplayName("Get Games Fail")
    public void getGamesFail() {

    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGamePass() {

    }

    @Test
    @DisplayName("Update Game Fail")
    public void updateGameFail() {

    }

    @Test
    @DisplayName("Clear Test")
    public void clearDatabase() {

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
}
