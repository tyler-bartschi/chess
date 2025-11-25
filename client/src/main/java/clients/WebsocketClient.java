package clients;

import chess.ChessGame;
import facades.ResponseException;
import facades.ServerFacade;
import ui.InputException;
import ui.UI.UICommand;
import static utils.ClientUtils.*;

import java.util.Arrays;

public class WebsocketClient implements Client {
    private final ServerFacade serverFacade;
    private boolean playing;
    private ChessGame currentBoard;

    public WebsocketClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UICommand execute(String[] tokens) throws InputException, ResponseException {
        String cmd = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        UICommand retCmd = UICommand.NO_CHANGE;

        switch (cmd) {
            case ("help") -> printHelp();
            case ("redraw") -> redraw(params);
            case ("leave") ->  {
                leaveGame(params);
                retCmd = UICommand.SET_AUTHENTICATED;
            }
            case ("move") -> makeMove(params);
            case ("resign") -> resign(params);
            case ("highlight") -> highlight(params);
            default -> throw new InputException("'" + cmd + "' is not a recognized command in this state. " +
                    "Run 'help' for a list of available commands.");
        }

        return retCmd;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void printHelp() {
        // prints the help string
    }

    public void redraw(String[] params) throws InputException {
        // prints the board again
    }

    public void leaveGame(String[] params) throws InputException, ResponseException {
        // leaves the game
    }

    public void makeMove(String[] params) throws InputException, ResponseException {
        // checks validity of move and makes move
    }

    public void resign(String[] params) throws InputException, ResponseException {
        // resigns from game
    }

    public void highlight(String[] params) throws InputException {
        // highlights the requested piece's valid moves
    }
}
