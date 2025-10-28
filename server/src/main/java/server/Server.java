package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import server.exceptions.*;
import server.handlers.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin server;

    public Server() {
        // initialize serializer
        Gson serializer = new Gson();


        // initialize dataAccess
        DataAccess dataAccess = new MemoryDataAccess();


        // initialize services
        final UserService userService = new UserService(dataAccess);
        final GameService gameService = new GameService(dataAccess);

        // initialize handlers
        final ClearHandler clearHandler = new ClearHandler(userService);
        final RegisterHandler registerHandler = new RegisterHandler(userService, serializer);
        final LoginHandler loginHandler = new LoginHandler(userService, serializer);
        final LogoutHandler logoutHandler = new LogoutHandler(userService);
        final CreateGameHandler createGameHandler = new CreateGameHandler(gameService, serializer);
        final JoinGameHandler joinGameHandler = new JoinGameHandler(gameService, serializer);
        final ListGamesHandler listGamesHandler = new ListGamesHandler(gameService, serializer);


        // initialize server
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", clearHandler::clear);
        server.post("user", registerHandler::register);
        server.post("session", loginHandler::login);
        server.delete("session", logoutHandler::logout);
        server.post("game", createGameHandler::createGame);
        server.put("game", joinGameHandler::joinGame);
        server.get("game", listGamesHandler::listGames);

        server.exception(InvalidRequestException.class, this::handleInvalidRequestException);
        server.exception(UnauthorizedException.class, this::handleUnauthorizedException);
        server.exception(AlreadyTakenException.class, this::handleAlreadyTakenException);

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

    private void handleUnauthorizedException(UnauthorizedException ex, Context ctx) {
        ctx.status(401).result(getErrorMessage(ex));
    }

    private void handleAlreadyTakenException(AlreadyTakenException ex, Context ctx) {
        ctx.status(403).result(getErrorMessage(ex));
    }

    private void handleUncaughtException(Exception ex, Context ctx) {
        ctx.status(500).result(getErrorMessage(ex));
    }

    private String getErrorMessage(Exception ex) {
        return String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
    }
}
