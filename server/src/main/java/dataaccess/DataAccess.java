package dataaccess;

import model.*;

public interface DataAccess {
    void clear();

    void createUser(UserData user);

    UserData getUser(String username);

    void createAuth(AuthData auth);

    AuthData getAuth(String username);

    AuthData getAuthByToken(String authToken);

    void deleteAuth(AuthData auth);

    void createGame(GameData game);

    GameData getGame(int gameID);
}
