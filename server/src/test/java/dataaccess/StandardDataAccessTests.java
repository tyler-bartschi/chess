package dataaccess;

import chess.ChessGame;
import kotlin._Assertions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import dataaccess.*;
import server.exceptions.*;
import service.requests.*;
import service.results.*;
import model.*;

import java.util.Collection;

public class StandardDataAccessTests {
    private static DataAccess testDataAccess;

    @BeforeAll
    public static void init() {
        testDataAccess = new SQLDataAccess();
    }

    @Test
    @DisplayName("Create User Success")
    public void createUserSucceed() {

    }

    @Test
    @DisplayName("Create User Failure")
    public void createUserFail() {
    }

    @Test
    @DisplayName("Get User Success")
    public void getUserPass() {

    }

    @Test
    @DisplayName("Get User Fail")
    public void getUserFail() {

    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthPass() {

    }

    @Test
    @DisplayName("Create Auth Fail")
    public void createAuthFail() {

    }

    @Test
    @DisplayName("Get Auth Success")
    public void getAuthPass() {

    }

    @Test
    @DisplayName("Get Auth Failure")
    public void getAuthFail() {

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
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {

    }

    @Test
    @DisplayName("Join Game Fail")
    public void joinGameFail() {

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
}
