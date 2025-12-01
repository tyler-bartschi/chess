package clients;

import facades.WebsocketException;
import ui.UI.UICommand;
import ui.InputException;
import facades.ResponseException;

public interface Client {
    UICommand execute(String[] tokens) throws InputException, ResponseException, WebsocketException;
}
