package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import server.exceptions.*;
import service.requests.*;
import service.results.*;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }

    public RegisterResult register(RegisterRequest user) throws AlreadyTakenException {
        if (dataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("This username is already taken");
        }

        UserData newUser = new UserData(user.username(), user.email(), hashPassword(user.password()));

        dataAccess.createUser(newUser);

        AuthData newAuth = new AuthData(user.username(), generateAuthToken());
        dataAccess.createAuth(newAuth);

        return new RegisterResult(newAuth.username(), newAuth.authToken());
    }

    public LoginResult login(LoginRequest req) throws UnauthorizedException {
        UserData user = dataAccess.getUser(req.username());
        if (user == null) {
            throw new UnauthorizedException("unauthorized -- invalid username or password");
        }
        if (!checkPassword(req.password(), user.password())) {
            throw new UnauthorizedException("unauthorized -- invalid username or password");
        }

        AuthData newAuth = new AuthData(user.username(), generateAuthToken());
        dataAccess.createAuth(newAuth);

        return new LoginResult(newAuth.username(), newAuth.authToken());
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
