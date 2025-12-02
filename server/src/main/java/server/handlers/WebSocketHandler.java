package server.handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import service.WebSocketService;
import websocket.commands.UserGameCommand;

public class WebSocketHandler extends AuthVerificationHandler {

    private final Gson serializer;
    private final WebSocketService webSocketService;

    public WebSocketHandler(WebSocketService webSocketService, Gson serializer) {
        this.serializer = serializer;
        this.webSocketService = webSocketService;
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
        UserGameCommand command  = serializer.fromJson(ctx.message(), UserGameCommand.class);

        switch (command.getCommandType()) {
            case UserGameCommand.CommandType.CONNECT -> webSocketService.connect(command, ctx.session);
            case UserGameCommand.CommandType.MAKE_MOVE -> webSocketService.makeMove(command);
            case UserGameCommand.CommandType.LEAVE -> webSocketService.leave(command);
            case UserGameCommand.CommandType.RESIGN -> webSocketService.resign(command);
        }

    }
}
