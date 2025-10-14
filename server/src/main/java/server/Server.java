package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        userService = new UserService();
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // ctx is a Context object that contains the request and the result. When you call result it sends
        // data back to the asking source
        server.delete("db", ctx -> ctx.result("{}"));
        // this::register is the same as ctx -> register(ctx)
        server.post("user", this::register);

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        String requestJson = ctx.body();
        var user = serializer.fromJson(requestJson, UserData.class);

        // call to the service and register
        var authData = userService.register(user);

        ctx.result(serializer.toJson(authData));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
