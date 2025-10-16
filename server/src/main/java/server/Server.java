package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import server.exceptions.*;
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
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler();
        createGameHandler = new CreateGameHandler();
        joinGameHandler = new JoinGameHandler();
        listGamesHandler = new ListGamesHandler();


        // initialize server
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", clearHandler::clear);
        server.post("user", registerHandler::register);
        server.post("session", loginHandler::login);

        server.exception(InvalidRequestException.class, this::handleInvalidRequestException);
        server.exception(AlreadyTakenException.class, this::handleAlreadyTakenException);
        server.exception(UnauthorizedException.class, this::handleUnauthorizedException);

        server.exception(Exception.class, this::handleUncaughtException);

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }

    private void handleInvalidRequestException(InvalidRequestException ex, Context ctx) {
        ctx.status(400).result(getErrorMessage(ex));
    }

    private void handleAlreadyTakenException(AlreadyTakenException ex, Context ctx) {
        ctx.status(403).result(getErrorMessage(ex));
    }

    private void handleUnauthorizedException(UnauthorizedException ex, Context ctx) {
        ctx.status(401).result(getErrorMessage(ex));
    }

    private void handleUncaughtException(Exception ex, Context ctx) {
        ctx.status(500).result(getErrorMessage(ex));
    }

    private String getErrorMessage(Exception ex) {
        return String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
    }
}
