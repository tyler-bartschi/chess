package ui;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UI {

    private AuthState state;
    private final String port;

    private enum AuthState {
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    public UI(String port) {
        state = AuthState.UNAUTHENTICATED;
        this.port = port;
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
            } catch (InputException ex) {
                printErrorMessage(ex.getMessage());
            } catch (Throwable ex) {
                printErrorMessage("An unidentified error occurred. Please try again.");
            }
        }

        System.out.println("Thanks for playing!");
    }

    private void printPrompt() {
        resetTextEffects();
        if (state == AuthState.UNAUTHENTICATED) {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_OUT] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        } else {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_IN] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        }
    }

    private void resetTextEffects() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_UNDERLINE + RESET_TEXT_ITALIC + RESET_TEXT_BLINKING + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private boolean evaluate(String line) throws InputException, Exception{
        resetTextEffects();
        try {
            String[] tokens = line.toLowerCase().split("\\s+");
            if (tokens.length == 0) {
                throw new InputException("No input provided, please type a command.");
            }
            String cmd = tokens[0];
            String[] rawParams = Arrays.copyOfRange(line.split("\\s+"), 1, tokens.length);
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "help" -> help();
                case "login" -> login(rawParams);
                case "register" -> register(rawParams);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "quit" -> false;
                default -> throw new InputException(cmd + " is not a recognized command. Run 'help' to see a list of available commands.");
            };

        } catch (Throwable ex) {
            // general error handling, make this more specific when I make the ServerFacade
        }
        return true;
    }

    private void printErrorMessage(String msg) {
        resetTextEffects();
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + msg);
    }

    private boolean help() {
        String caseSensitive = SET_TEXT_COLOR_MAGENTA + "NOTE: fields surrounded by <> are " + SET_TEXT_BOLD + "case sensitive" + RESET_TEXT_BOLD_FAINT;
        if (state == AuthState.UNAUTHENTICATED) {
            printBlueAndWhite("register <USERNAME> <PASSWORD> <EMAIL> ", "- to create an account " + caseSensitive);
            printBlueAndWhite("login <USERNAME> <PASSWORD> ", "- login to play chess " + caseSensitive);
            printBlueAndWhite("quit ", "- quits the chess program");
            printBlueAndWhite("help", " - display all possible commands");
        }

        if (state == AuthState.AUTHENTICATED) {
            printBlueAndWhite("create <NAME> ", "- create a game, NAME will be made all lower case");
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

    private boolean login(String[] params) {
        return true;
    }

    private boolean register(String[] params) {
        state = AuthState.AUTHENTICATED;
        return true;
    }

    private boolean logout() {
        return true;
    }

    private boolean create(String[] params) {
        return true;
    }

    private boolean list() {
        return true;
    }

    private boolean join(String[] params) {
        return true;
    }

    private boolean observe(String[] params) {
        return true;
    }

    private void printBlueAndWhite(String first, String second) {
        System.out.println(EMPTY + SET_TEXT_COLOR_BLUE + first + SET_TEXT_COLOR_WHITE + second);
    }
}
