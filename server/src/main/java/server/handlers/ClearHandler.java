package server.handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;

public class ClearHandler {
    private final UserService userService;

    public ClearHandler(UserService userService) {
        this.userService = userService;
    }

    public void clear(Context ctx) throws DataAccessException {
        userService.clear();
        ctx.result("{}");
    }
}
