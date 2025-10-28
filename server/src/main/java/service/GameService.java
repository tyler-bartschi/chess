package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import server.exceptions.*;
import service.requests.*;
import service.results.*;
import chess.ChessGame;
import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListResult listGames(ListRequest req) throws UnauthorizedException {
        verifyAuthToken(req.authToken());
        Collection<GameData> games = dataAccess.getAllGames();
        ArrayList<AbbrGameData> listOfGames = new ArrayList<>();
        for (GameData game : games) {
            listOfGames.add(new AbbrGameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return new ListResult(listOfGames);
    }

    public SuccessEmptyResult joinGame(JoinRequest req) throws UnauthorizedException, InvalidRequestException, AlreadyTakenException, DataAccessException {
        verifyAuthToken(req.authToken());
        String username = dataAccess.getAuthByToken(req.authToken()).username();

        TeamColor teamColor = req.playerColor().equals("WHITE") ? TeamColor.WHITE : TeamColor.BLACK;

        GameData existingGame = dataAccess.getGame(req.gameID());
        if (existingGame == null) {
            throw new InvalidRequestException("No game by that gameID");
        }
        if (teamColor == TeamColor.WHITE) {
            setWhiteUsername(username, existingGame);
        } else {
            setBlackUsername(username, existingGame);
        }

        return new SuccessEmptyResult();
    }

    public CreateResult createGame(CreateRequest req) throws UnauthorizedException, DataAccessException {
        verifyAuthToken(req.authToken());
        GameDataNoID game = createGameData(req.gameName());
        GameData createdGame = dataAccess.createGame(game);
        return new CreateResult(createdGame.gameID());
    }

    private void setWhiteUsername(String username, GameData existingGame) throws AlreadyTakenException, DataAccessException {
        if (existingGame.whiteUsername() != null) {
            throw new AlreadyTakenException("White player already taken");
        }
        dataAccess.updateGame(existingGame.gameID(), new GameData(existingGame.gameID(), username, existingGame.blackUsername(),
                existingGame.gameName(), existingGame.game()));
    }

    private void setBlackUsername(String username, GameData existingGame) throws AlreadyTakenException, DataAccessException {
        if (existingGame.blackUsername() != null) {
            throw new AlreadyTakenException("Black player already taken");
        }
        dataAccess.updateGame(existingGame.gameID(), new GameData(existingGame.gameID(), existingGame.whiteUsername(), username,
                existingGame.gameName(), existingGame.game()));
    }

    private void verifyAuthToken(String authToken) throws UnauthorizedException {
        AuthData auth = dataAccess.getAuthByToken(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    private GameDataNoID createGameData(String gameName) {
        return new GameDataNoID(null, null, gameName, new ChessGame());
    }
}
