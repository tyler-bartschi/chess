package facades;

import chess.*;
import com.google.gson.Gson;
import ui.BoardRenderer;
import ui.InputException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final BoardRenderer boardRenderer;
    private final HttpClient httpClient;
    private final Gson serializer;

    private final String serverUrl;
    private String authToken;
    private String username;
    private HashMap<String, Integer> gameIDs;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
        boardRenderer = new BoardRenderer();
        httpClient = HttpClient.newHttpClient();
        serializer = new Gson();
        gameIDs = new HashMap<>();
    }

    public String login(String[] params) throws InputException, ResponseException {
        if (params.length < 2) {
            throw new InputException("Must provide both <USERNAME> and <PASSWORD>");
        }

        try {
            var body = Map.of("username", params[0], "password", params[1]);
            String json = serializer.toJson(body);
            String urlString = serverUrl + "/session";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (processBodyForAuthentication(response)) {
                return username + " successfully logged in.";
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException("Failure in HTTP: " + ex.getMessage());
        }

        return "";
    }

    public String register(String[] params) throws InputException, ResponseException {
        if (params.length < 3) {
            throw new InputException("Must provide <USERNAME>, <PASSWORD> and <EMAIL>");
        }

        try {
            var body = Map.of("username", params[0], "password", params[1], "email", params[2]);
            String json = serializer.toJson(body);
            String urlString = serverUrl + "/user";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (processBodyForAuthentication(response)) {
                return username + " successfully registered.";
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException("Failure in HTTP: " + ex.getMessage());
        }
        return "";
    }

    public String logout(String[] params) throws ResponseException {
        // invalidate all fields
        return "";
    }

    public String create(String[] params) throws InputException, ResponseException {
        return "";
    }

    public String list(String[] params) throws ResponseException {
        return "";
    }

    public String join (String[] params) throws InputException, ResponseException {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return boardRenderer.renderGameBoard(ChessGame.TeamColor.WHITE, board);
    }

    public String observe(String[] params) throws InputException, ResponseException {
        return "";
    }

    private boolean processBodyForAuthentication(HttpResponse<String> response) throws ResponseException {
        var responseBody = serializer.fromJson(response.body(), Map.class);
        if (response.statusCode() == 200) {
            username = responseBody.get("username").toString();
            authToken = responseBody.get("authToken").toString();
            return true;
        } else {
            throw new ResponseException(responseBody.get("message").toString());
        }
    }
}
