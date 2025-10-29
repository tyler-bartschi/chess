package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    /**
     * Clears the entire database
     */
    void clear();

    /**
     * Adds the provided user to the database
     *
     * @param user UserData containing the username, password, and email of a user
     * @throws DataAccessException if the username already exists in the database
     */
    void createUser(UserData user) throws DataAccessException;

    /**
     * Returns the UserData associated with the provided username
     *
     * @param username Username of the requested user
     * @return UserData of the user, null if the username does not exist
     */
    UserData getUser(String username);

    /**
     * Adds the authorization data for a particular user into the database
     *
     * @param auth The AuthData of the user
     * @throws DataAccessException if the authToken already exists in the database
     */
    void createAuth(AuthData auth) throws DataAccessException;

    /**
     * Gets the authorization data associated with a particular username
     *
     * @param username username of the user authorization data being requested
     * @return AuthData if the user has a valid authorization, null if not
     */
    AuthData getAuth(String username);

    /**
     * Gets the authorization data associated with a particular authToken
     *
     * @param authToken authToken of the user authorization data being requested
     * @return AuthData if the user has a valid authorization, null if not
     */
    AuthData getAuthByToken(String authToken);

    /**
     * Deletes the authData of a user
     *
     * @param auth AuthData associated with the given user
     * @throws DataAccessException if the AuthData has already been deleted
     */
    void deleteAuth(AuthData auth) throws DataAccessException;

    /**
     * Creates a game and assigns a gameID within the database
     *
     * @param game GameDataNoID, which includes all game data except for the gameID
     * @return the full GameData if successful, including the gameID, null if not successful
     */
    GameData createGame(GameDataNoID game);

    /**
     * Gets a game by the requested gameID
     *
     * @param gameID ID of the requested game
     * @return GameData associated with the ID if it exists, null if not
     */
    GameData getGame(int gameID);

    /**
     * Updates a game, given a gameID and the new GameData to update it with
     *
     * @param gameID gameID of the game to update
     * @param game   the full new GameData (including the gameID)
     * @throws DataAccessException if the gameID is invalid
     */
    void updateGame(int gameID, GameData game) throws DataAccessException;

    /**
     * Gets all games in the database
     *
     * @return a Collection of GameData representing all the games in the database, and an empty collection if there's none
     */
    Collection<GameData> getAllGames();

    /**
     * Creates a new game in the database, with the ID given, primarily used for testing purposes
     *
     * @param game the full GameData to be put into the database
     * @throws DataAccessException if the gameID in the GameData exists
     */
    void createGameWithID(GameData game) throws DataAccessException;
}
