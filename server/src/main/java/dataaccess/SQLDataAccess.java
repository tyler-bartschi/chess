package dataaccess;

import com.google.gson.Gson;
import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLDataAccess implements DataAccess {

    private final String[] clearStatements = {
            "DROP TABLE games",
            "DROP TABLE auth",
            "DROP TABLE users"
    };

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
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : clearStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            DatabaseManager.createTables();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.email());
                preparedStatement.setString(3, user.password());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String resUsername = "";
        String resEmail = "";
        String resPass = "";
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, email, password FROM users WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (ResultSet result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        resUsername = result.getString(1);
                        resEmail = result.getString(2);
                        resPass = result.getString(3);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        if (!resUsername.isEmpty() && !resEmail.isEmpty() && !resPass.isEmpty()) {
            return new UserData(resUsername, resEmail, resPass);
        }
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {

        } catch (SQLException ex) {

        }

        return null;
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public GameData createGame(GameDataNoID game) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {

    }

    @Override
    public void createGameWithID(GameData game) throws DataAccessException {

    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return List.of();
    }
}
