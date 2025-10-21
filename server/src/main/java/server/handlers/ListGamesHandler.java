package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import server.exceptions.UnauthorizedException;
import service.GameService;
import service.requests.ListRequest;
import service.results.ListResult;

public class ListGamesHandler extends AuthVerificationHandler {

    private final GameService gameService;
    private final Gson serializer;

    public ListGamesHandler(GameService gameService, Gson serializer) {
        this.gameService = gameService;
        this.serializer = serializer;
    }

    public void listGames(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("Authorization");
        verifyAuth(authToken);

        ListResult res = gameService.listGames(new ListRequest(authToken));

        ctx.result(serializer.toJson(res));
    }
}
