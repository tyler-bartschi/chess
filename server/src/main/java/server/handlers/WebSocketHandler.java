package server.handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;

public class WebSocketHandler extends AuthVerificationHandler {

    private final Gson serializer;

    public WebSocketHandler(Gson serializer) {
        this.serializer = serializer;
    }

    public void createConnection(WsConfig ws) {
        ws.onConnect(ctx -> {
           ctx.enableAutomaticPings();
           System.out.println("Websocket connection made");
        });
        ws.onMessage(this::onMessage);
        ws.onClose(_ -> System.out.println("Websocket closed"));
    }

    public void onMessage(WsMessageContext ctx) {
        // for now, it just echoes what is received
        ctx.send("WebSocket response: " + ctx.message());
    }
}
