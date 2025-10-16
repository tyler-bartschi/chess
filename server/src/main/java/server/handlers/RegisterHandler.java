package server.handlers;

import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.Context;

import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidRequestException;

import service.UserService;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws InvalidRequestException, AlreadyTakenException {
        var serializer = new Gson();
        String requestJson = ctx.body();
        checkDataValidity(serializer, requestJson);
        var user = serializer.fromJson(requestJson, RegisterRequest.class);

        // call to the service and register
        RegisterResult authData = userService.register(user);

        ctx.result(serializer.toJson(authData));

    }

    private void checkDataValidity(Gson serializer, String requestBody) throws InvalidRequestException {
        var providedData = serializer.fromJson(requestBody, Map.class);
        if (providedData.get("username") == null) {
            throw new InvalidRequestException("No username provided");
        }
        if (providedData.get("email") == null) {
            throw new InvalidRequestException("No email provided");
        }
        if (providedData.get("password") == null) {
            throw new InvalidRequestException("No password provided");
        }
    }
}
