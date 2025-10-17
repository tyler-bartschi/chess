package server.handlers;

import io.javalin.http.Context;
import server.exceptions.UnauthorizedException;
import service.UserService;
import service.requests.LogoutRequest;
import service.results.SuccessEmptyResult;

public class LogoutHandler {

    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void logout(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new UnauthorizedException("Unauthorized. Are you already logged out?");
        }

        SuccessEmptyResult res = userService.logout(new LogoutRequest(authToken));

        ctx.result("{}");
    }
}
