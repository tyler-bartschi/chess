package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import server.exceptions.InvalidRequestException;
import server.exceptions.UnauthorizedException;
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

    public void onMessage(WsMessageContext ctx) throws UnauthorizedException, DataAccessException, InvalidRequestException {
        // for now, it just echoes what is received
        UserGameCommand command  = serializer.fromJson(ctx.message(), UserGameCommand.class);
        verifyAuth(command.getAuthToken());

        switch (command.getCommandType()) {
            case UserGameCommand.CommandType.CONNECT -> webSocketService.connect(command, ctx.session);
            case UserGameCommand.CommandType.MAKE_MOVE -> webSocketService.makeMove(command, ctx.session);
            case UserGameCommand.CommandType.LEAVE -> webSocketService.leave(command, ctx.session);
            case UserGameCommand.CommandType.RESIGN -> webSocketService.resign(command, ctx.session);
        }

    }
}
