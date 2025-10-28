package server.handlers;

import java.util.Map;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidRequestException;

import service.UserService;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

public class RegisterHandler {

    private final UserService userService;
    private final Gson serializer;

    public RegisterHandler(UserService userService, Gson serializer) {
        this.userService = userService;
        this.serializer = serializer;
    }

    public void register(Context ctx) throws InvalidRequestException, AlreadyTakenException, DataAccessException {
        String requestJson = ctx.body();
        checkDataValidity(serializer, requestJson);
        RegisterRequest req = serializer.fromJson(requestJson, RegisterRequest.class);

        // call to the service and register
        RegisterResult res = userService.register(req);

        ctx.result(serializer.toJson(res));

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
