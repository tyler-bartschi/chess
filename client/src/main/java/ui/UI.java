package ui;

import chess.*;
import facades.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UI {

    private AuthState state;
    private final ServerFacade serverFacade;

    private enum AuthState {
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    public UI(int port) {
        state = AuthState.UNAUTHENTICATED;
        serverFacade = new ServerFacade(port);
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
        System.out.println("Thanks for playing!");
    }

    private boolean evaluate(String line) throws InputException, ResponseException {
        resetTextEffects();
        String[] tokens = line.toLowerCase().split("\\s+");
        if (tokens[0].isEmpty()) {
            throw new InputException("No input provided, please type a command.");
        }
        String cmd = tokens[0];
        String[] rawParams = Arrays.copyOfRange(line.split("\\s+"), 1, tokens.length);
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "help" -> help();
            case "login" -> login(rawParams);
            case "register" -> register(rawParams);
            case "logout" -> logout(params);
            case "create" -> create(rawParams);
            case "list" -> list(params);
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> quit();
            default ->
                    throw new InputException("'" + cmd + "'" + " is not a recognized command. Run 'help' to see a list of available commands.");
        };
    }

    private boolean quit() {
        if (state == AuthState.AUTHENTICATED) {
            try {
                logout(new String[]{});
            } catch (Throwable ignored) {
            }
        }
        resetTextEffects();
        return false;
    }

    private boolean help() {
        String caseSensitive = SET_TEXT_COLOR_MAGENTA + "NOTE: fields surrounded by <> are "
                + SET_TEXT_BOLD + "case sensitive" + RESET_TEXT_BOLD_FAINT;
        if (state == AuthState.UNAUTHENTICATED) {
            printBlueAndWhite("register <USERNAME> <PASSWORD> <EMAIL> ", "- to create an account " + caseSensitive);
            printBlueAndWhite("login <USERNAME> <PASSWORD> ", "- login to play chess " + caseSensitive);
            printBlueAndWhite("quit ", "- quits the chess program");
            printBlueAndWhite("help", " - display all possible commands");
        }

        if (state == AuthState.AUTHENTICATED) {
            printBlueAndWhite("create <NAME> ", "- create a game");
            printBlueAndWhite("list ", "- list all games");
            printBlueAndWhite("join <ID> [WHITE|BLACK] ", "- join a game");
            printBlueAndWhite("observe <ID> ", "- observe a game");
            printBlueAndWhite("logout ", "- logs out of the chess program");
            printBlueAndWhite("quit ", "- quits the chess program");
            printBlueAndWhite("help ", "- display all possible commands");
        }
        System.out.println();
        return true;
    }

    private boolean login(String[] params) throws InputException, ResponseException {
        throwIfAuthenticated("You are already logged in.");
        printSuccessMessage(serverFacade.login(params));
        setStateAuthenticated();
        return true;
    }

    private boolean register(String[] params) throws InputException, ResponseException {
        throwIfAuthenticated("You cannot register while logged in.");
        printSuccessMessage(serverFacade.register(params));
        setStateAuthenticated();
        return true;
    }

    private boolean logout(String[] params) throws InputException, ResponseException {
        throwIfUnauthenticated("You are already logged out.");
        printSuccessMessage(serverFacade.logout(params));
        setStateUnauthenticated();
        return true;
    }

    private boolean create(String[] params) throws InputException, ResponseException {
        throwIfUnauthenticated("Must be logged in to create a game.");
        printSuccessMessage(serverFacade.create(params));
        return true;
    }

    private boolean list(String[] params) throws InputException, ResponseException {
        throwIfUnauthenticated("Must be logged in to list games.");
        System.out.println(serverFacade.list(params));
        return true;
    }

    private boolean join(String[] params) throws InputException, ResponseException {
        throwIfUnauthenticated("Must be logged in to join a game");
        System.out.println(serverFacade.join(params));
        return true;
    }

    private boolean observe(String[] params) throws InputException, ResponseException {
        throwIfUnauthenticated("Must be logged in to observe a game.");
        System.out.println(serverFacade.observe(params));
        return true;
    }

    private void setStateAuthenticated() {
        state = AuthState.AUTHENTICATED;
    }

    private void setStateUnauthenticated() {
        state = AuthState.UNAUTHENTICATED;
    }

    private void throwIfUnauthenticated(String message) throws InputException {
        if (state == AuthState.UNAUTHENTICATED) {
            throw new InputException(message);
        }
    }

    private void throwIfAuthenticated(String message) throws InputException {
        if (state == AuthState.AUTHENTICATED) {
            throw new InputException(message);
        }
    }

    private void resetTextEffects() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_UNDERLINE + RESET_TEXT_ITALIC + RESET_TEXT_BLINKING + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private void printErrorMessage(String msg) {
        resetTextEffects();
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + msg + "\n");
    }

    private void printBlueAndWhite(String first, String second) {
        System.out.println(EMPTY + SET_TEXT_COLOR_BLUE + first + SET_TEXT_COLOR_WHITE + second);
    }

    private void printPrompt() {
        resetTextEffects();
        if (state == AuthState.UNAUTHENTICATED) {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_OUT] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        } else {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_IN] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        }
    }

    private void printSuccessMessage(String message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message + "\n");
    }
}
