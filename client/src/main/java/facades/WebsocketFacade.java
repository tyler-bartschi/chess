package facades;

import chess.ChessMove;
import clients.WebsocketClient.ServerMessageObserver;
import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import ui.InputException;
import websocket.commands.UserGameCommand;

import java.net.URI;

public class WebsocketFacade {

    private Session session;

    private final String serverUrl;
    private final Gson serializer;

    private ServerMessageObserver serverMessageObserver;
    private String authToken;
    private int gameID;

    public WebsocketFacade(int port) {
        serverMessageObserver = null;
        serializer = new Gson();
        serverUrl = "ws://localhost:" + port + "/ws";
    }

    public void setServerMessageObserver(ServerMessageObserver serverMessageObserver) {
        this.serverMessageObserver = serverMessageObserver;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void createConnection() {
        try {
            URI uri = new URI(serverUrl);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    serverMessageObserver.onMessage(message);
                }
            });
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public void sendConnectCommand() throws WebsocketException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendMessage(command);
    }

    public void sendMakeMoveCommand(ChessMove move) throws WebsocketException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        sendMessage(command);
    }

    public void sendLeaveCommand() throws WebsocketException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendMessage(command);
    }

    public void sendResignCommand() throws WebsocketException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendMessage(command);
    }

    private void sendMessage(UserGameCommand command) throws WebsocketException {
        String message = serializer.toJson(command);
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception ex) {
            throw new WebsocketException("An error occurred with websocket: " + ex.getMessage());
        }
    }
}
