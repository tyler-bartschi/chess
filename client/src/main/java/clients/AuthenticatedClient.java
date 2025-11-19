package clients;

import facades.ResponseException;
import facades.ServerFacade;
import ui.InputException;
import ui.UI.UICommand;

import java.util.Arrays;

import static utils.ClientUtils.*;

public class AuthenticatedClient implements Client {
    private final ServerFacade serverFacade;

    public AuthenticatedClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UICommand execute(String[] tokens) throws InputException, ResponseException {
        String cmd = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        UICommand retCmd = UICommand.NO_CHANGE;

        switch (cmd) {
            case ("help") -> printHelp();
            case ("logout") -> {
                logout(params);
                retCmd = UICommand.SET_UNAUTHENTICATED;
            }
            case ("create") -> create(params);
            case ("list") -> list(params);
            case ("join") -> {
                join(params);
                // retCmd = UICommand.SET_PLAYING;
            }
            case ("observe") -> {
                observe(params);
                // retCmd = UICommand.SET_PLAYING? SET_OBSERVING?
            }
            case ("quit") -> {
                logout(new String[]{});
                retCmd = UICommand.END;
            }
            default -> throw new InputException("'" + cmd + "'" + " is not a recognized command in this state. " +
                    "Run 'help' for a list of available commands.");
        }

        return retCmd;
    }

    private void printHelp() {
        printBlueAndWhite("create <NAME> ", "- create a game");
        printBlueAndWhite("list ", "- list all games");
        printBlueAndWhite("join <ID> [WHITE|BLACK] ", "- join a game");
        printBlueAndWhite("observe <ID> ", "- observe a game");
        printBlueAndWhite("logout ", "- logs out of the chess program");
        printBlueAndWhite("quit ", "- quits the chess program");
        printBlueAndWhite("help ", "- display all possible commands");
        System.out.println();
    }

    private void logout(String[] params) throws InputException, ResponseException {
        if (params.length > 0) {
            throw new InputException("Too many parameters. 'logout' does not require any parameters");
        }
        printSuccessMessage(serverFacade.logout());
    }

    private void create(String[] params) throws InputException, ResponseException {

    }

    private void list(String[] params) throws InputException, ResponseException {
        // make it so that one of the params can be a number, the number of games to list
    }

    private void join(String[] params) throws InputException, ResponseException {

    }

    private void observe(String[] params) throws InputException, ResponseException {

    }

}
