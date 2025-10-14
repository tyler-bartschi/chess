package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;

    }

    public AuthData register(UserData user) throws Exception {
        if (dataAccess.getUser(user.username()) != null) {
            // look at petshop for handling exceptions
            throw new Exception("already exists");
        }
        return new AuthData(user.username(), generateAuthToken());
    }

    private String generateAuthToken() {
        return "xyz";
    }
}
