package clients;

import facades.ServerFacade;
import facades.ResponseException;
import ui.InputException;
import ui.UI.UICommand;
import facades.requests.*;

import java.util.Arrays;

import static utils.ClientUtils.*;

public class UnauthenticatedClient implements Client {

    private final ServerFacade serverFacade;

    public UnauthenticatedClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UICommand execute(String[] tokens) throws InputException, ResponseException {
        String cmd = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        UICommand retCmd = UICommand.NO_CHANGE;

        switch (cmd) {
            case "help" -> printHelp();
            case "login" -> {
                login(params);
                retCmd = UICommand.SET_AUTHENTICATED;
            }
            case "register" -> {
                register(params);
                retCmd = UICommand.SET_AUTHENTICATED;
            }
            case "quit" -> retCmd = UICommand.END;
            default -> throw new InputException("'" + cmd + "' is not a recognized command in this state. " +
                    "Run 'help' to view a list of commands");
        }

        return retCmd;
    }

    private void printHelp() {
        printBlueAndWhite("register <USERNAME> <PASSWORD> <EMAIL> ", "- to create an account " + CASE_SENSITIVE);
        printBlueAndWhite("login <USERNAME> <PASSWORD> ", "- login to play chess " + CASE_SENSITIVE);
        printBlueAndWhite("quit ", "- quits the chess program");
        printBlueAndWhite("help", " - display all possible commands");
        System.out.println();
    }

    private void login(String[] params) throws InputException, ResponseException {
        if (params.length < 2) {
            throw new InputException("Must provide both <USERNAME> and <PASSWORD>");
        } else if (params.length > 2) {
            throw new InputException("Too many parameters provided. Please only provide <USERNAME> and <PASSWORD>");
        }
        printSuccessMessage(serverFacade.login(new LoginRequest(params[0], params[1])));
    }

    private void register(String[] params) throws InputException, ResponseException {
        if (params.length < 3) {
            throw new InputException("Must provide <USERNAME>, <PASSWORD> and <EMAIL>");
        } else if (params.length > 3) {
            throw new InputException("Too many parameters provided. Please only provide <USERNAME>, <PASSWORD> and <EMAIL>");
        }
        printSuccessMessage(serverFacade.register(new RegisterRequest(params[0], params[1], params[2])));
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

}
