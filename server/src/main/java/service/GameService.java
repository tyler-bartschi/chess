package service;

import dataaccess.DataAccess;
import io.javalin.http.Context;
import model.*;
import server.exceptions.*;
import service.requests.*;
import service.results.*;
import chess.ChessGame;

public class GameService {

    private final DataAccess dataAccess;
    private int gamesCreated;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        gamesCreated = 0;
    }

    public CreateResult createGame(CreateRequest req) throws UnauthorizedException {
        verifyAuthToken(req.authToken());
        GameData game = createGameData(req.gameName());
        dataAccess.createGame(game);
        return new CreateResult(game.gameID());
    }

    private void verifyAuthToken(String authToken) throws UnauthorizedException {
        AuthData auth = dataAccess.getAuthByToken(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    private GameData createGameData(String gameName) {
        int gameID = generateValidGameID();
        return new GameData(gameID, null, null, gameName, new ChessGame());
    }

    private int generateValidGameID() {
        gamesCreated++;
        int gameID = gamesCreated;
        GameData possibleGame = dataAccess.getGame(gameID);
        while (possibleGame != null) {
            gamesCreated++;
            gameID = gamesCreated;
            possibleGame = dataAccess.getGame(gameID);
        }
        return gameID;
    }
}
