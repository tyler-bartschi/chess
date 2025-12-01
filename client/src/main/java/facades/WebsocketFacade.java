package facades;

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
    private String username;

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

    public void setUsername(String username) {
        this.username = username;
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

}
