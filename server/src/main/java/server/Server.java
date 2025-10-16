package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidRequestException;
import server.handlers.*;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;

    // handlers
    private final ClearHandler clearHandler;
    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;

    public Server() {
        // initialize dataAccess
        DataAccess dataAccess = new MemoryDataAccess();

        // initialize services
        userService = new UserService(dataAccess);

        // initialize handlers
        clearHandler = new ClearHandler(userService);
        registerHandler = new RegisterHandler(userService);
        loginHandler = new LoginHandler();
        logoutHandler = new LogoutHandler();
        createGameHandler = new CreateGameHandler();
        joinGameHandler = new JoinGameHandler();
        listGamesHandler = new ListGamesHandler();


        // initialize server
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // ctx is a Context object that contains the request and the result. When you call result it sends
        // data back to the asking source
        server.delete("db", ctx -> ctx.result("{}"));
        // this::register is the same as ctx -> register(ctx)
        server.post("user", registerHandler::register);

        server.exception(InvalidRequestException.class, this::handleInvalidRequestException);
        server.exception(AlreadyTakenException.class, this::handleAlreadyTakenException);

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }

    private void handleInvalidRequestException(InvalidRequestException ex, Context ctx) {
        var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
        ctx.status(400).result(msg);
    }

    private void handleAlreadyTakenException(AlreadyTakenException ex, Context ctx) {
        var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
        ctx.status(403).result(msg);
    }
}
