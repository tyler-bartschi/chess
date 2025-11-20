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
                    "Run 'help' to view a list of available commands");
        }

        return retCmd;
    }

    private void printHelp() {
        printBlueAndWhite("register <USERNAME> <PASSWORD> <EMAIL> ", "- to create an account ");
        printBlueAndWhite("login <USERNAME> <PASSWORD> ", "- login to play chess ");
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
}
