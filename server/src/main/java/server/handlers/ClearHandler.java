package server.handlers;

import io.javalin.http.Context;
import service.UserService;

public class ClearHandler {
    private final UserService userService;

    public ClearHandler(UserService userService) {
        this.userService = userService;
    }

    public void clear(Context ctx) {
        userService.clear();
        ctx.result("{}");
    }
}
