package clients;

import facades.ResponseException;
import facades.ServerFacade;
import ui.InputException;
import ui.UI.UICommand;

public class AuthenticatedClient implements Client {
    private final ServerFacade serverFacade;

    public AuthenticatedClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UICommand execute(String[] tokens) throws InputException, ResponseException {
        return UICommand.NO_CHANGE;
    }
}
