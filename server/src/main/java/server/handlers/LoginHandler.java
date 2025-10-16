package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import server.exceptions.*;

import java.util.Map;

import service.UserService;

import service.requests.LoginRequest;
import service.results.LoginResult;

public class LoginHandler {

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void login(Context ctx) throws InvalidRequestException, UnauthorizedException {
        var serializer = new Gson();
        String requestJson = ctx.body();
        checkDataValidity(serializer, requestJson);

        LoginRequest req = serializer.fromJson(requestJson, LoginRequest.class);

        LoginResult res = userService.login(req);

        ctx.result(serializer.toJson(res));
    }

    private void checkDataValidity(Gson serializer, String requestBody) throws InvalidRequestException {
        var providedData = serializer.fromJson(requestBody, Map.class);
        if (providedData.get("username") == null) {
            throw new InvalidRequestException("Must provide a username to login");
        }
        if (providedData.get("password") == null) {
            throw new InvalidRequestException("Must provide a password to login");
        }
    }
}
