package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear();

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username);

    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String username);

    AuthData getAuthByToken(String authToken);

    void deleteAuth(AuthData auth) throws DataAccessException;

    GameData createGame(GameDataNoID game) throws DataAccessException;

    GameData getGame(int gameID);

    void updateGame(int gameID, GameData game) throws DataAccessException;

    Collection<GameData> getAllGames();

    void createGameWithID(GameData game) throws DataAccessException;
}
