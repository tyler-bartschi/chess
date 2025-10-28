package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import server.exceptions.*;

import java.util.Map;

import service.requests.JoinRequest;
import service.results.SuccessEmptyResult;

public class JoinGameHandler extends AuthVerificationHandler {

    private final GameService gameService;
    private final Gson serializer;

    public JoinGameHandler(GameService gameService, Gson serializer) {
        this.gameService = gameService;
        this.serializer = serializer;
    }

    public void joinGame(Context ctx) throws InvalidRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        String authToken = ctx.header("Authorization");
        verifyAuth(authToken);

        Map dataMap = serializer.fromJson(ctx.body(), Map.class);
        verifyData(dataMap);

        JoinRequest req = new JoinRequest(authToken, dataMap.get("playerColor").toString().toUpperCase(),
                ((Double) dataMap.get("gameID")).intValue());
        SuccessEmptyResult res = gameService.joinGame(req);

        ctx.result(serializer.toJson(res));

    }

    private void verifyData(Map dataMap) throws InvalidRequestException {
        var color = dataMap.get("playerColor");
        if (!(color instanceof String)) {
            throw new InvalidRequestException("Must provide a playerColor field");
        }
        color = ((String) color).toUpperCase();
        if (!(color.equals("BLACK") || color.equals("WHITE"))) {
            throw new InvalidRequestException("Must provide a valid color: WHITE/BLACK");
        }
        var gameID = dataMap.get("gameID");
        if (gameID == null || gameID.toString().isEmpty()) {
            throw new InvalidRequestException("Must provide a gameID field");
        }
    }
}
