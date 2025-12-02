package service;

import dataaccess.DataAccess;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

public class WebSocketService {

    private final DataAccess dataAccess;

    public WebSocketService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void connect(UserGameCommand command, Session session) {

    }

    public void makeMove(UserGameCommand command) {

    }

    public void leave(UserGameCommand command) {

    }

    public void resign(UserGameCommand command) {

    }
}
