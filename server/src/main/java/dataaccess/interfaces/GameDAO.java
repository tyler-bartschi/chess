package dataaccess.interfaces;

import java.util.Collection;

import dataaccess.DataAccessException;
import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    /**
     * Clears all GameData from the database
     */
    public void clear();

    /**
     * Adds a game to the database given a GameData object
     *
     * @param game GameData object to add to database
     * @throws DataAccessException if gameID is already taken
     */
    public void createGame(GameData game) throws DataAccessException;

    /**
     * Gets a game by a given gameID
     *
     * @param gameID gameID to locate game
     * @return GameData object if found, null if not
     */
    public GameData getGame(String gameID);

    /**
     * List all the games currently stored in the database
     *
     * @return Collection of all GameData objects
     */
    public Collection<GameData> listGames();

    /**
     * Updates a GameData that matches a gameID with a given game
     *
     * @param gameID gameID
     * @param game   ChessGame representing current game
     * @throws DataAccessException if gameID cannot be found
     */
    public void updateGame(String gameID, ChessGame game) throws DataAccessException;
    // maybe change this?

    /**
     * Adds a user to an already existing game
     *
     * @param gameID   ID of the game to join
     * @param username player's username
     * @param white    true if joining white team, false if joining black team
     * @throws DataAccessException if gameID doesn't exist
     */
    public void joinGame(String gameID, String username, boolean white) throws DataAccessException;
}
