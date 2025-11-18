package clients;

import facades.ServerFacade;
import ui.InputException;
import ui.UI.UICommand;

import static ui.EscapeSequences.*;

public class UnauthenticatedClient implements Client{

    private final ServerFacade serverFacade;

    public UnauthenticatedClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UICommand execute(String[] tokens) {
        return UICommand.NO_CHANGE;
    }

//    private boolean quit() {
//        if (state == UI.AuthState.AUTHENTICATED) {
//            try {
//                logout(new String[]{});
//            } catch (Throwable ignored) {
//            }
//        }
//        resetTextEffects();
//        return false;
//    }
//
//    private boolean help() {
//        String caseSensitive = SET_TEXT_COLOR_MAGENTA + "NOTE: fields surrounded by <> are "
//                + SET_TEXT_BOLD + "case sensitive" + RESET_TEXT_BOLD_FAINT;
//        if (state == UI.AuthState.UNAUTHENTICATED) {
//            printBlueAndWhite("register <USERNAME> <PASSWORD> <EMAIL> ", "- to create an account " + caseSensitive);
//            printBlueAndWhite("login <USERNAME> <PASSWORD> ", "- login to play chess " + caseSensitive);
//            printBlueAndWhite("quit ", "- quits the chess program");
//            printBlueAndWhite("help", " - display all possible commands");
//        }
//
//        if (state == UI.AuthState.AUTHENTICATED) {
//            printBlueAndWhite("create <NAME> ", "- create a game");
//            printBlueAndWhite("list ", "- list all games");
//            printBlueAndWhite("join <ID> [WHITE|BLACK] ", "- join a game");
//            printBlueAndWhite("observe <ID> ", "- observe a game");
//            printBlueAndWhite("logout ", "- logs out of the chess program");
//            printBlueAndWhite("quit ", "- quits the chess program");
//            printBlueAndWhite("help ", "- display all possible commands");
//        }
//        System.out.println();
//        return true;
//    }
//
//    private boolean login(String[] params) throws InputException, ResponseException {
//        throwIfAuthenticated("You are already logged in.");
//        printSuccessMessage(serverFacade.login(params));
//        setStateAuthenticated();
//        return true;
//    }
//
//    private boolean register(String[] params) throws InputException, ResponseException {
//        throwIfAuthenticated("You cannot register while logged in.");
//        printSuccessMessage(serverFacade.register(params));
//        setStateAuthenticated();
//        return true;
//    }
//
//    private boolean logout(String[] params) throws InputException, ResponseException {
//        throwIfUnauthenticated("You are already logged out.");
//        printSuccessMessage(serverFacade.logout(params));
//        setStateUnauthenticated();
//        return true;
//    }
//
//    private boolean create(String[] params) throws InputException, ResponseException {
//        throwIfUnauthenticated("Must be logged in to create a game.");
//        printSuccessMessage(serverFacade.create(params));
//        return true;
//    }
//
//    private boolean list(String[] params) throws InputException, ResponseException {
//        throwIfUnauthenticated("Must be logged in to list games.");
//        System.out.println(serverFacade.list(params));
//        return true;
//    }
//
//    private boolean join(String[] params) throws InputException, ResponseException {
//        throwIfUnauthenticated("Must be logged in to join a game");
//        System.out.println(serverFacade.join(params));
//        return true;
//    }
//
//    private boolean observe(String[] params) throws InputException, ResponseException {
//        throwIfUnauthenticated("Must be logged in to observe a game.");
//        System.out.println(serverFacade.observe(params));
//        return true;
//    }

//    private void throwIfUnauthenticated(String message) throws InputException {
//        if (state == UI.AuthState.UNAUTHENTICATED) {
//            throw new InputException(message);
//        }
//    }
//
//    private void throwIfAuthenticated(String message) throws InputException {
//        if (state == UI.AuthState.AUTHENTICATED) {
//            throw new InputException(message);
//        }
//    }

//    private void printBlueAndWhite(String first, String second) {
//        System.out.println(EMPTY + SET_TEXT_COLOR_BLUE + first + SET_TEXT_COLOR_WHITE + second);
//    }

//    private void printSuccessMessage(String message) {
//        System.out.println(SET_TEXT_COLOR_GREEN + message + "\n");
//    }

//    return switch (cmd) {
//        case "help" -> help();
//        case "login" -> login(rawParams);
//        case "register" -> register(rawParams);
//        case "logout" -> logout(params);
//        case "create" -> create(rawParams);
//        case "list" -> list(params);
//        case "join" -> join(params);
//        case "observe" -> observe(params);
//        case "quit" -> quit();
//        default ->
//                throw new InputException("'" + cmd + "'" + " is not a recognized command. Run 'help' to see a list of available commands.");
//    };
}
