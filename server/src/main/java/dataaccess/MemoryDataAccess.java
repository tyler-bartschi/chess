package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        auths.clear();
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
        auths.put(auth.username(), auth);
    }

    @Override
    public AuthData getAuth(String username) {
        return auths.get(username);
    }
}
