package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import server.exceptions.InvalidRequestException;
import server.exceptions.UnauthorizedException;
import service.GameService;
import service.requests.CreateRequest;
import service.results.CreateResult;

import java.util.Map;

public class CreateGameHandler {

    private final GameService gameService;
    private final Gson serializer;

    public CreateGameHandler(GameService gameService, Gson serializer) {
        this.gameService = gameService;
        this.serializer = serializer;
    }

    public void createGame(Context ctx) throws InvalidRequestException, UnauthorizedException {
        String authToken = ctx.header("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new UnauthorizedException("Unauthorized.");
        }

        Map gameNameMap = serializer.fromJson(ctx.body(), Map.class);
        verifyData(gameNameMap);

        CreateResult res = gameService.createGame(new CreateRequest(authToken, gameNameMap.get("gameName").toString()));
        ctx.result(serializer.toJson(res));
    }

    private void verifyData(Map gameNameMap) throws InvalidRequestException {
        if (gameNameMap.get("gameName") == null || gameNameMap.get("gameName") == "") {
            throw new InvalidRequestException("Must provide a game name");
        }
    }
}
