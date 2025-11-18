package clients;

import facades.ServerFacade;
import ui.UI.UICommand;

public class PlayingClient implements Client {
    private final ServerFacade serverFacade;

    public PlayingClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UICommand execute(String[] tokens) {
        return UICommand.NO_CHANGE;
    }
}
