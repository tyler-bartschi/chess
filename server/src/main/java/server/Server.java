package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
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
        var req = serializer.fromJson(requestJson, Map.class);

        // call to the service and register

        var res = Map.of("username", req.get("username"), "authToken", "yzx");
        ctx.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
