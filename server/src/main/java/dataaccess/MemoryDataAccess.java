package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authsByUser = new HashMap<>();
    private final HashMap<String, AuthData> authsByToken = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        authsByUser.clear();
        authsByToken.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth) {
        authsByUser.put(auth.username(), auth);
        authsByToken.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String username) {
        return authsByUser.get(username);
    }

    @Override
    public AuthData getAuthByToken(String authToken) {
        return authsByToken.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) {
        authsByUser.remove(auth.username());
        authsByToken.remove(auth.authToken());
    }

    @Override
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
}
