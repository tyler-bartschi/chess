package dataaccess;

import com.google.gson.Gson;
import model.*;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import chess.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLDataAccess implements DataAccess {

    private final Gson serializer;

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
        serializer = new Gson();
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
        String resAuthToken = "";
        String resUsername = "";
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (ResultSet res = preparedStatement.executeQuery()) {
                    if (res.next()) {
                        resAuthToken = res.getString(1);
                        resUsername = res.getString(2);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        if (!resAuthToken.isEmpty() && !resUsername.isEmpty()) {
            return new AuthData(resUsername, resAuthToken);
        }

        return null;
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        String resAuthToken = "";
        String resUsername = "";
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (ResultSet res = preparedStatement.executeQuery()) {
                    if (res.next()) {
                        resAuthToken = res.getString(1);
                        resUsername = res.getString(2);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        if (!resAuthToken.isEmpty() && !resUsername.isEmpty()) {
            return new AuthData(resUsername, resAuthToken);
        }

        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        int rowsDeleted = 0;
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM auth WHERE authToken=? AND username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                rowsDeleted = preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        if (rowsDeleted < 1) {
            throw new DataAccessException("No authentication to delete. Are you already logged out?");
        }
        if (rowsDeleted > 1) {
            throw new DataAccessException("Deleted multiple rows");
        }
    }

    @Override
    public GameData createGame(GameDataNoID game) throws DataAccessException {
        int gameID = 0;
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                String gameText = serializer.toJson(game.game());
                preparedStatement.setString(4, gameText);
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        gameID = resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        if (gameID != 0) {
            return getGame(gameID);
        }

        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        int resGameID = 0;
        String resWhiteUsername = "";
        String resBlackUsername = "";
        String resGameName = "";
        String resGame = "";
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games where id=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet res = preparedStatement.executeQuery()) {
                    if (res.next()) {
                        resGameID = res.getInt(1);
                        resWhiteUsername = res.getString(2);
                        resBlackUsername = res.getString(3);
                        resGameName = res.getString(4);
                        resGame = res.getString(5);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        if (resGameID != 0 && !resGameName.isEmpty() && !resGame.isEmpty()) {
            ChessGame chessGame = serializer.fromJson(resGame, ChessGame.class);
            return new GameData(resGameID, resWhiteUsername, resBlackUsername, resGameName, chessGame);
        }

        return null;
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        int rowsUpdated = 0;
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE id=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, serializer.toJson(game.game()));
                preparedStatement.setInt(5, gameID);
                rowsUpdated = preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        if (rowsUpdated < 1) {
            throw new DataAccessException("Invalid gameID to update");
        }
        if (rowsUpdated > 1) {
            throw new DataAccessException("Critical failure: more games than 1 updated at once");
        }
    }

    @Override
    public void createGameWithID(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, serializer.toJson(game.game()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (ResultSet res = preparedStatement.executeQuery()) {
                    while (res.next()) {
                        int gameID = res.getInt(1);
                        String whiteUsername = res.getString(2);
                        String blackUsername = res.getString(3);
                        String gameName = res.getString(4);
                        ChessGame game = serializer.fromJson(res.getString(5), ChessGame.class);
                        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        return games;
    }
}
