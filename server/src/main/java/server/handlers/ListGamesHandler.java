package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import server.exceptions.UnauthorizedException;
import service.GameService;
import service.results.ListResult;

public class ListGamesHandler {

    private final GameService gameService;
    private final Gson serializer;

    public ListGamesHandler(GameService gameService, Gson serializer) {
        this.gameService = gameService;
        this.serializer = serializer;
    }

    public ListResult listGames(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("Authorization");
        return null;
    }
}
