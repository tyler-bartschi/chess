package ui;

import clients.*;
import facades.*;

import static utils.ClientUtils.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UI {

    private AuthState state;
    private final Client unauthenticatedClient;
    private final Client authenticatedClient;
    private final WebsocketClient websocketClient;

    private Client currentClient;
    private final ServerFacade serverFacade;

    private enum AuthState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        PLAYING
    }

    public enum UICommand {
        SET_UNAUTHENTICATED,
        SET_AUTHENTICATED,
        SET_PLAYING,
        SET_OBSERVING,
        NO_CHANGE,
        END
    }

    public UI(int port) {
        state = AuthState.UNAUTHENTICATED;
        serverFacade = new ServerFacade(port);
        unauthenticatedClient = new UnauthenticatedClient(serverFacade);
        authenticatedClient = new AuthenticatedClient(serverFacade);
        websocketClient = new WebsocketClient(serverFacade);
        currentClient = unauthenticatedClient;
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to chess! Type 'help' to get started. " + BLACK_KING);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                running = evaluate(line);
            } catch (InputException | ResponseException | RuntimeException ex) {
                printErrorMessage(ex.getMessage());
            } catch (Throwable ex) {
                printErrorMessage("An unidentified error occurred. Please try again.");
            }
        }
        resetTextEffects();
        System.out.println("Thanks for playing!");
    }

    private boolean evaluate(String line) throws InputException, ResponseException {
        resetTextEffects();
        String[] tokens = line.split("\\s+");
        if (tokens[0].isEmpty()) {
            throw new InputException("No input provided, please type a command.");
        }

        UICommand result = currentClient.execute(tokens);
        boolean ret = true;

        switch (result) {
            case SET_UNAUTHENTICATED:
                setStateUnauthenticated();
                break;
            case SET_AUTHENTICATED:
                setStateAuthenticated();
                break;
            case SET_PLAYING:
                setStateWebsocket(true);
                break;
            case SET_OBSERVING:
                setStateWebsocket(false);
                break;
            case NO_CHANGE:
                break;
            case END:
                ret = false;
                break;
            default:
                throw new RuntimeException("Client side error occurred - invalid UICommand returned");
        }

        return ret;
    }

    private void setStateAuthenticated() {
        state = AuthState.AUTHENTICATED;
        currentClient = authenticatedClient;
    }

    private void setStateUnauthenticated() {
        state = AuthState.UNAUTHENTICATED;
        currentClient = unauthenticatedClient;
    }

    private void setStateWebsocket(boolean playing) {
        state = AuthState.PLAYING;
        websocketClient.setPlaying(playing);
        currentClient = websocketClient;

    }

    private void printErrorMessage(String msg) {
        resetTextEffects();
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + msg + "\n");
    }

    private void printPrompt() {
        resetTextEffects();
        if (state == AuthState.UNAUTHENTICATED) {
            printPromptHelper("[LOGGED_OUT] ");
        } else if (state == AuthState.AUTHENTICATED) {
            printPromptHelper("[LOGGED_IN] ");
        } else if (state == AuthState.PLAYING) {
            printPromptHelper("[PLAYING] ");
        }
    }

    private void printPromptHelper(String msg1) {
        System.out.print(SET_TEXT_BOLD + msg1 + RESET_TEXT_BOLD_FAINT + ">>> ");
    }
}
