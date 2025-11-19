package client;

import facades.*;
import ui.InputException;
import org.junit.jupiter.api.*;
import server.Server;
import facades.requests.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String serverUrl;

    // for clearing the database
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        // clear the database for each test
        String urlString = serverUrl + "/db";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .DELETE()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Login Success")
    public void loginHappy() {
        String username = "loginHappyUsername";
        String password = "loginHappyPassword";
        addUserToDatabase(username, password);
        String result = assertDoesNotThrow(() -> facade.login(new LoginRequest(username, password)));
        assertNotNull(result);
        assertContains(username, result);
    }

    @Test
    @DisplayName("Login Failure")
    public void loginSad() {
        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("doesNotExist", "doesNotExist")));
    }

    @Test
    @DisplayName("Register Success")
    public void registerHappy() {
        String registerUsername = "registerHappyUser";

        String result = assertDoesNotThrow(() -> facade.register(new RegisterRequest(registerUsername, "registerHappyPass", "registerHappyEmail")));
        assertNotNull(result);
        assertContains(registerUsername, result);
    }

    @Test
    @DisplayName("Register Failure")
    public void registerSad() {
        String username = "registerAlreadyTakenUsername";
        addUserToDatabase(username, "password", "email");
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest(username, "password", "email")));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutHappy() {
        String username = "logoutUsername";
        addUserToDatabase(username, "logoutPassword", "logoutEmail");
        String result = assertDoesNotThrow(() -> facade.logout());
        assertNotNull(result);
        assertContains(username, result);
    }

    @Test
    @DisplayName("Logout Failure")
    public void logoutSad() {
        addUserToDatabase("random", "random");
        assertDoesNotThrow(() -> facade.logout());
        assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    @DisplayName("Create Success")
    public void createHappy() {
        String gameName = "createHappyGame";
        addUserToDatabase("createUser", "createPassword");
        String result = assertDoesNotThrow(() -> facade.create(new CreateRequest(gameName)));
        assertNotNull(result);
        assertContains(gameName, result);
    }

    @Test
    @DisplayName("Create Failure")
    public void createSad() {
        addUserToDatabase("random", "random");
        logoutUser();
        assertThrows(ResponseException.class, () -> facade.create(new CreateRequest("gameName")));
    }

    @Test
    @DisplayName("List Success")
    public void listHappy() {
        String username = "listHappyUsername";
        String gameName1 = "gameNameFirst";
        String gameName2 = "gameNameSecond";
        addUserToDatabase(username, "password");
        addGameToDatabase(gameName1);
        addGameToDatabase(gameName2);

        String result = assertDoesNotThrow(() -> facade.list(-1));
        assertNotNull(result);
        assertContains(gameName1, result);
        assertContains(gameName2, result);
    }

    @Test
    @DisplayName("List Failure")
    public void listSad() {
        addUserToDatabase("username", "password");
        logoutUser();
        assertThrows(ResponseException.class, () -> facade.list(-1));
    }

    @Test
    @DisplayName("Join Success")
    public void joinHappy() {
        String username = "joinUsername";
        String color = "WHITE";
        addUserToDatabase(username, "password");
        addGameToDatabase("firstGameName");
        listGames();
        String result = assertDoesNotThrow(() -> facade.join(new JoinRequest(1, color)));
        assertNotNull(result);
        assertContains(username, result);
        assertContains(color, result);
        assertTrue(result.length() > 50);

        String listResult = listGames();
        assertNotNull(listResult);
        assertContains(username, listResult);
    }

    @Test
    @DisplayName("Join Failure")
    public void joinSad() {
        addUserToDatabase("myUsername", "myPassword");
        addGameToDatabase("firstGameName");
        listGames();
        assertThrows(InputException.class, () -> facade.join(new JoinRequest(17, "white")));
        assertDoesNotThrow(() -> facade.join(new JoinRequest(1, "white")));
        logoutUser();
        assertThrows(InputException.class, () -> facade.join(new JoinRequest(1, "white")));
        addUserToDatabase("aDifferentUser", "myPasswordOnceAgain");
        listGames();
        assertThrows(ResponseException.class, () -> facade.join(new JoinRequest(1, "white")));
    }

    @Test
    @DisplayName("Observe Success")
    public void observeHappy() {
        String username = "observeUsername";
        addUserToDatabase(username, "password");
        addGameToDatabase("thisGame");
        listGames();
        String result = assertDoesNotThrow(() -> facade.observe(1));
        assertNotNull(result);
        assertContains(username, result);
        assertTrue(result.length() > 50);
    }

    @Test
    @DisplayName("Observe Failure")
    public void observeSad() {
        addUserToDatabase("observeSad", "password");
        addGameToDatabase("myGame");
        listGames();
        assertThrows(InputException.class, () -> facade.observe(7));
        // does not yet assert authentication because no endpoint exists on the server to just get one game
    }

    private void assertContains(String expected, String observed) {
        assertTrue(() -> observed.contains(expected));
    }

    private void addUserToDatabase(String username, String password, String email) {
        assertDoesNotThrow(() -> facade.register(new RegisterRequest(username, password, email)));
    }

    private void addUserToDatabase(String username, String password) {
        assertDoesNotThrow(() -> facade.register(new RegisterRequest(username, password, "email")));
    }

    private void addGameToDatabase(String gameName) {
        assertDoesNotThrow(() -> facade.create(new CreateRequest(gameName)));
    }

    private void logoutUser() {
        assertDoesNotThrow(() -> facade.logout());
    }

    private String listGames() {
        return assertDoesNotThrow(() -> facade.list(-1));
    }
}
