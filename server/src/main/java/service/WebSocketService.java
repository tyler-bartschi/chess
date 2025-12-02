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

    public void connect(UserGameCommand command, Session session) throws UnauthorizedException, DataAccessException, InvalidRequestException {
        AuthData auth = getAuthAndVerify(command.getAuthToken());
        GameData game = getGameAndVerify(command.getGameID());

        int gameID = command.getGameID();
        String username = auth.username();

        connectionContainer.addUser(gameID, username, session);
        String message = serializer.toJson(new LoadGameMessage(LOAD_GAME, game.game()));
        try {
            session.getRemote().sendString(message);
        } catch (Throwable ex) {
            System.out.println("An error occured trying to send a websocket message: " + ex.getMessage());
        }

        String serializedNotification = serializer.toJson(new NotificationMessage(NOTIFICATION, notificationForConnection(game, username)));
        connectionContainer.sendToAllExcept(gameID, username, serializedNotification);
    }

    public void makeMove(UserGameCommand command) {

    }

    public void leave(UserGameCommand command) throws UnauthorizedException, DataAccessException, InvalidRequestException {
        AuthData auth = getAuthAndVerify(command.getAuthToken());
        GameData game = getGameAndVerify(command.getGameID());

        int gameID = command.getGameID();
        String username = auth.username();

        connectionContainer.removeUser(gameID, username);

    }

    public void resign(UserGameCommand command) {

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
            throw new InvalidRequestException("That game does not exist!");
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
}
