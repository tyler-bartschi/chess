package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import server.exceptions.AlreadyTakenException;
import service.requests.*;
import service.results.*;

import java.util.UUID;

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

        UserData newUser = new UserData(user.username(), user.email(), user.password());

        dataAccess.createUser(newUser);

        AuthData newAuth = new AuthData(user.username(), generateAuthToken());
        dataAccess.createAuth(newAuth);

        return new RegisterResult(newAuth.username(), newAuth.authToken());
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
