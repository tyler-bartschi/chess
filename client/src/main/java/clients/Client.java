package clients;

import ui.UI.UICommand;

public interface Client {
    UICommand execute(String[] tokens);
}
