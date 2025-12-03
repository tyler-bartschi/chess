package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.exceptions.InvalidRequestException;
import server.exceptions.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.util.Collection;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketService {

    private final DataAccess dataAccess;
    private final ConnectionContainer connectionContainer;
    private final Gson serializer;

    public WebSocketService(DataAccess dataAccess, Gson serializer) {
        this.dataAccess = dataAccess;
        this.serializer = serializer;
        connectionContainer = new ConnectionContainer();
    }

    public void connect(UserGameCommand command, Session session) throws DataAccessException {
        if (checkCommand(command, session)) {
            return;
        }

        AuthData auth = dataAccess.getAuthByToken(command.getAuthToken());
        GameData game = dataAccess.getGame(command.getGameID());

        int gameID = command.getGameID();
        String username = auth.username();

        connectionContainer.addUser(gameID, username, session);
        String message = serializer.toJson(new LoadGameMessage(LOAD_GAME, game.game()));
        sendMessage(session, message);

        String serializedNotification = serializer.toJson(new NotificationMessage(NOTIFICATION, notificationForConnection(game, username)));
        connectionContainer.sendToAllExcept(gameID, username, serializedNotification);
    }

    public void makeMove(UserGameCommand command, Session session) throws DataAccessException {
        if (checkCommand(command, session)) {
            return;
        }

        AuthData auth = dataAccess.getAuthByToken(command.getAuthToken());
        GameData game = dataAccess.getGame(command.getGameID());
        int gameID = command.getGameID();
        String username = auth.username();

        if (game.gameName() != null && game.gameName().contains("OVER")) {
            String errorMessage = "ERROR: This game is over. Moves can no longer be made.";
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, errorMessage)));
            return;
        }

        boolean isWhite = game.whiteUsername() != null && username.equals(game.whiteUsername());
        ChessGame.TeamColor playerColor = isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor opposingColor = isWhite ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (verifyMoveValidity(command, session, game, playerColor)) {
            return;
        }

        try {
            ChessGame newGame = game.game();
            newGame.makeMove(command.getMove());

            String loadMessage = serializer.toJson(new LoadGameMessage(LOAD_GAME, newGame));
            connectionContainer.sendToAll(gameID, loadMessage);

            String notificationMessage = serializer.toJson(new NotificationMessage(NOTIFICATION,
                    username + " moved " + command.getMove().toString()));
            connectionContainer.sendToAllExcept(gameID, username, notificationMessage);

            GameData updatedGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), newGame);

            if (newGame.isInCheckmate(opposingColor)) {
                // is in checkmate, end game and send notification
                updatedGame = setGameOver(updatedGame);
                String endMessage = serializer.toJson(new NotificationMessage(NOTIFICATION, opposingColor + " is now in CHECKMATE. Game over."));
                connectionContainer.sendToAll(gameID, endMessage);
            } else if (newGame.isInStalemate(opposingColor)) {
                // is in stalemate, end game and send notification
                updatedGame = setGameOver(updatedGame);
                String endMessage = serializer.toJson(new NotificationMessage(NOTIFICATION, "Game is now in STALEMATE. Game over."));
                connectionContainer.sendToAll(gameID, endMessage);
            } else if (newGame.isInCheck(opposingColor)) {
                // opposing is in check, send notification
                String newNotification = serializer.toJson(new NotificationMessage(NOTIFICATION, opposingColor + " is now in CHECK."));
                connectionContainer.sendToAll(gameID, newNotification);
            }

            dataAccess.updateGame(gameID, updatedGame);

        } catch (InvalidMoveException ex) {
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, "ERROR: that is not a valid move")));
        }
    }

    public void leave(UserGameCommand command, Session session) throws DataAccessException {
        if (checkCommand(command, session)) {
            return;
        }

        AuthData auth = dataAccess.getAuthByToken(command.getAuthToken());
        GameData game = dataAccess.getGame(command.getGameID());
        int gameID = command.getGameID();
        String username = auth.username();

        connectionContainer.removeUser(gameID, username);
        session.close();

        String notification;
        if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
            GameData newGame = new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game());
            dataAccess.updateGame(gameID, newGame);
            notification = username + " has left the game. Was WHITE";
        } else if (game.blackUsername() != null && game.blackUsername().equals(username)) {
            GameData newGame = new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game());
            dataAccess.updateGame(gameID, newGame);
            notification = username + " has left the game. Was BLACK";
        } else {
            notification = username + " has left the game. Was observer";
        }

        connectionContainer.sendToAll(gameID, serializer.toJson(new NotificationMessage(NOTIFICATION, notification)));
    }

    public void resign(UserGameCommand command, Session session) throws DataAccessException {
        if (checkCommand(command, session)) {
            return;
        }

        AuthData auth = dataAccess.getAuthByToken(command.getAuthToken());
        GameData game = dataAccess.getGame(command.getGameID());
        int gameID = command.getGameID();
        String username = auth.username();

        if (game.gameName() != null && game.gameName().contains("OVER")) {
            String errorMessage = "ERROR: This game is over, you cannot resign";
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, errorMessage)));
            return;
        }

        if (isObserver(username, game)) {
            String errorMessage = "ERROR: Cannot resign, you are an observer";
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, errorMessage)));
            return;
        }

        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName() + " [OVER]", game.game());
        dataAccess.updateGame(gameID, newGame);

        String notification = username + " has resigned. The game is now over.";
        connectionContainer.sendToAll(gameID, serializer.toJson(new NotificationMessage(NOTIFICATION, notification)));
    }

    private boolean isObserver(String username, GameData game) {
        if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
            return false;
        }
        return game.blackUsername() == null || !game.blackUsername().equals(username);
    }

    private GameData setGameOver(GameData oldGame) {
        return new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName() + " [OVER]", oldGame.game());
    }

    private boolean verifyMoveValidity(UserGameCommand command, Session session, GameData game, ChessGame.TeamColor playerColor) {
        ChessGame currentGame = game.game();
        ChessMove move = command.getMove();
        if (move == null) {
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, "ERROR: must provide a move")));
            return true;
        }

        ChessGame.TeamColor teamTurn = currentGame.getTeamTurn();

        if (teamTurn != playerColor) {
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, "ERROR: it is not your turn")));
            return true;
        }

        ChessPosition startOfMove = move.getStartPosition();
        Collection<ChessMove> possibleMoves = currentGame.validMoves(startOfMove);

        if (!possibleMoves.contains(move)) {
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, "ERROR: that is not a valid move")));
            return true;
        }

        return false;
    }

    private boolean checkCommand(UserGameCommand command, Session session) throws DataAccessException {
        try {
            AuthData auth = getAuthAndVerify(command.getAuthToken());
            GameData game = getGameAndVerify(command.getGameID());
        } catch (UnauthorizedException ex) {
            // bad auth
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, "ERROR: invalid authentication")));
            return true;
        } catch (InvalidRequestException ex) {
            // bad game
            sendMessage(session, serializer.toJson(new ErrorMessage(ERROR, "ERROR: invalid gameID")));
            return true;
        }
        return false;
    }

    private AuthData getAuthAndVerify(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData auth = dataAccess.getAuthByToken(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        return auth;
    }

    private GameData getGameAndVerify(int gameID) throws InvalidRequestException, DataAccessException {
        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new InvalidRequestException("Bad gameID");
        }
        return game;
    }

    private String notificationForConnection(GameData game, String username) {
        String notification;

        if (username.equals(game.whiteUsername())) {
            // joined as a white player
            notification = username + " joined as WHITE";
        } else if (username.equals(game.blackUsername())) {
            // joined as a black player
            notification = username + " joined as BLACK";
        } else {
            // joined as an observer
            notification = username + " joined as an observer";
        }
        return notification;
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (Throwable ex) {
            System.out.println("An error occured trying to send a websocket message: " + ex.getMessage());
        }
    }
}
