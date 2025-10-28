package dataaccess;

import model.*;

import java.util.Collection;
import java.util.List;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException ex) {
            System.out.println("Failure to create database or tables");
            throw new RuntimeException("Failure to create database or tables.");
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuthByToken(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public GameData createGame(GameDataNoID game) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {

    }

    @Override
    public void createGameWithID(GameData game) throws DataAccessException {

    }

    @Override
    public Collection<GameData> getAllGames() {
        return List.of();
    }
}
