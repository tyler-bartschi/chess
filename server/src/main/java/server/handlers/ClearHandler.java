package server.handlers;

import service.UserService;

public class ClearHandler {
    private final UserService userService;

    public ClearHandler(UserService userService) {
        this.userService = userService;
    }
}
