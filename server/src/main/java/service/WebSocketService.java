package service;

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

    public void makeMove(UserGameCommand command, Session session) {

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

        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName() + " [OVER]", game.game());
        dataAccess.updateGame(gameID, newGame);

        String notification = username + " has resigned. The game is now over.";
        connectionContainer.sendToAll(gameID, serializer.toJson(new NotificationMessage(NOTIFICATION, notification)));
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

        if (game.whiteUsername().equals(username)) {
            // joined as a white player
            notification = username + " joined as WHITE";
        } else if (game.blackUsername().equals(username)) {
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
