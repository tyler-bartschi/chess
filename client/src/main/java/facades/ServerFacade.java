package facades;

import chess.*;
import com.google.gson.Gson;
import ui.BoardRenderer;
import ui.InputException;
import facades.requests.*;

import static ui.EscapeSequences.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFacade {

    private final BoardRenderer boardRenderer;
    private final HttpClient httpClient;
    private final Gson serializer;
    private final String serverUrl;

    private String authToken;
    private String username;
    private final HashMap<Integer, Integer> gameIDs;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
        boardRenderer = new BoardRenderer();
        httpClient = HttpClient.newHttpClient();
        serializer = new Gson();
        gameIDs = new HashMap<>();
    }

    public String login(LoginRequest req) throws ResponseException {
        try {
            String json = serializer.toJson(req);
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

    public String register(RegisterRequest req) throws ResponseException {
        try {
            String json = serializer.toJson(req);
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
        ignoreAdditionalParametersMessage(params);

        try {
            String urlString = serverUrl + "/session";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .header("Authorization", authToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String result = username + " logged out successfully.";
                username = "";
                authToken = "";
                gameIDs.clear();
                return result;
            } else {
                var responseBody = serializer.fromJson(response.body(), Map.class);
                throw new ResponseException(responseBody.get("message").toString());
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException("Failure in HTTP: " + ex.getMessage());
        }
    }

    public String create(String[] params) throws InputException, ResponseException {
        if (params.length < 1) {
            throw new InputException("Must provide <NAME>");
        }

        try {
            var body = Map.of("gameName", params[0]);
            String json = serializer.toJson(body);
            String urlString = serverUrl + "/game";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(BodyPublishers.ofString(json))
                    .header("Authorization", authToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            var responseBody = serializer.fromJson(response.body(), Map.class);
            if (response.statusCode() == 200) {
                return "Created game " + params[0];
            } else {
                throw new ResponseException(responseBody.get("message").toString());
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException("Failure in HTTP: " + ex.getMessage());
        }
    }

    public String list(String[] params) throws ResponseException {
        ignoreAdditionalParametersMessage(params);

        try {
            String urlString = serverUrl + "/game";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .GET()
                    .header("Authorization", authToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                var responseBody = serializer.fromJson(response.body(), GameListResponse.class);
                StringBuilder stringBuilder = new StringBuilder();
                List<Game> games = responseBody.games();

                if (games.isEmpty()) {
                    return SET_TEXT_COLOR_GREEN + "No games created yet.\n";
                }

                int count = 1;
                for (Game game : games) {
                    if (count > 1) {
                        stringBuilder.append("\n");
                    }
                    gameIDs.put(count, game.gameID());
                    stringBuilder.append(SET_TEXT_COLOR_GREEN).append(count).append(RESET_TEXT_COLOR).append(". ").append(game.gameName())
                            .append("\n   WHITE: ").append(game.whiteUsername() == null ? "" : game.whiteUsername())
                            .append("\n   BLACK: ").append(game.blackUsername() == null ? "" : game.blackUsername()).append("\n");
                    count++;
                }
                return stringBuilder.toString();

            } else {
                var responseBody = serializer.fromJson(response.body(), Map.class);
                throw new ResponseException(responseBody.get("message").toString());
            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException("Failure in HTTP: " + ex.getMessage());
        }
    }

    public String join(String[] params) throws InputException, ResponseException {
        if (params.length < 2) {
            throw new InputException("Must provide <ID> and <WHITE|BLACK>");
        }
        if (!params[0].matches("\\d+")) {
            throw new InputException("Must provide a valid <ID>");
        }
        if (!gameIDs.containsKey(Integer.parseInt(params[0]))) {
            throw new InputException("Must provide a valid <ID>");
        }

        try {
            var body = Map.of("playerColor", params[1], "gameID", gameIDs.get(Integer.parseInt(params[0])));
            String json = serializer.toJson(body);
            String urlString = serverUrl + "/game";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .PUT(BodyPublishers.ofString(json))
                    .header("Authorization", authToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(SET_TEXT_COLOR_GREEN).append(username).append(" successfully joined as ").
                        append(params[1].toUpperCase()).append(RESET_TEXT_COLOR).append("\n\n");

                ChessBoard board = new ChessBoard();
                board.resetBoard();

                if (params[1].equalsIgnoreCase("WHITE")) {
                    stringBuilder.append(boardRenderer.renderGameBoard(ChessGame.TeamColor.WHITE, board));
                } else {
                    stringBuilder.append(boardRenderer.renderGameBoard(ChessGame.TeamColor.BLACK, board));
                }

                return stringBuilder.toString();
            } else {
                var responseBody = serializer.fromJson(response.body(), Map.class);
                throw new ResponseException(responseBody.get("message").toString());

            }

        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException("Failure in HTTP: " + ex.getMessage());
        }
    }

    public String observe(String[] params) throws InputException, ResponseException {
        if (params.length < 1) {
            throw new InputException("Must provide <ID>");
        }

        if (!params[0].matches("\\d+")) {
            throw new InputException("Must provide a valid <ID>");
        }

        if (!gameIDs.containsKey(Integer.parseInt(params[0]))) {
            throw new InputException("Must provide a valid <ID>");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SET_TEXT_COLOR_GREEN).append(username).append(" observing game ").
                append(params[0]).append(RESET_TEXT_COLOR).append("\n\n");

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        stringBuilder.append(boardRenderer.renderGameBoard(ChessGame.TeamColor.WHITE, board));
        return stringBuilder.toString();
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

    private void ignoreAdditionalParametersMessage(String[] params) {
        if (params.length > 0) {
            System.out.println(SET_TEXT_COLOR_RED + "Ignoring additional provided parameters..." + RESET_TEXT_COLOR);
        }
    }
}
