package dataaccess;

import model.*;

import java.util.HashMap;
import java.util.Collection;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authsByUser = new HashMap<>();
    private final HashMap<String, AuthData> authsByToken = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    private int gamesCreated;

    public MemoryDataAccess() {
        gamesCreated = 0;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        authsByUser.clear();
        authsByToken.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("This username is already taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authsByToken.containsKey(auth.authToken())) {
            throw new DataAccessException("AuthToken already exists");
        }
        authsByUser.put(auth.username(), auth);
        authsByToken.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return authsByUser.get(username);
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return authsByToken.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        if (!authsByUser.containsKey(auth.username())) {
            throw new DataAccessException("Authorization already removed for this user");
        }
        authsByUser.remove(auth.username());
        authsByToken.remove(auth.authToken());
    }

    @Override
    public GameData createGame(GameDataNoID newGame) throws DataAccessException {
        int gameID = generateValidGameID();
        GameData game = new GameData(gameID, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), newGame.game());
        games.put(gameID, game);
        return game;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("That game does not exist!");
        }
        games.put(gameID, game);
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void createGameWithID(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("That gameID already exists");
        }
        games.put(game.gameID(), game);
    }

    private int generateValidGameID() throws DataAccessException {
        gamesCreated++;
        int gameID = gamesCreated;
        GameData possibleGame = getGame(gameID);
        while (possibleGame != null) {
            gamesCreated++;
            gameID = gamesCreated;
            possibleGame = getGame(gameID);
        }
        return gameID;
    }

}
