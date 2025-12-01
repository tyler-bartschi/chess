package clients;

import chess.ChessGame;
import facades.ResponseException;
import facades.WebsocketFacade;
import ui.InputException;
import ui.UI.UICommand;
import ui.BoardRenderer;
import static utils.ClientUtils.*;

import java.util.Arrays;

public class WebsocketClient implements Client {
    private final WebsocketFacade websocketFacade;
    private final BoardRenderer boardRenderer;
    private boolean playing;
    private ChessGame.TeamColor teamColor;
    private ChessGame currentGame;

    public WebsocketClient(WebsocketFacade websocketFacade) {
        this.websocketFacade = websocketFacade;
        boardRenderer = new BoardRenderer();
        this.currentGame = null;
    }

    public class ServerMessageObserver {

        public ServerMessageObserver() {
        }

        public void onMessage(String message) {
            // parse and display server messages
        }

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

    public void setTeamColor(ChessGame.TeamColor color) {
        teamColor = color;
    }

    public void activate(String username, String authToken) {
        // activate the websocketFacade
        websocketFacade.setServerMessageObserver(new ServerMessageObserver());
        websocketFacade.setUsername(username);
        websocketFacade.setAuthToken(authToken);
        websocketFacade.createConnection();
    }

    private void printHelp() {
        printBlueAndWhite("redraw ", "- redraws the chess board");
        printBlueAndWhite("leave ", "- leaves the game");
        printBlueAndWhite("move <StartRow><StartColumn> <EndRow><EndColumn> ", "- makes a chess move");
        printBlueAndWhite("resign ", "- forfeits the game, other player wins");
        printBlueAndWhite("highlight <row> <column> ", "- highlights possible moves for the piece on the given row and column");
        printBlueAndWhite("help ", "- display all possible commands");
    }

    private void redraw(String[] params) throws InputException {
        if (currentGame == null) {
            throw new InputException("No board to redraw.");
        }
        if (params.length != 0) {
            throw new InputException("Too many parameters provided. 'redraw' takes no parameters");
        }

        boardRenderer.renderGameBoard(teamColor, currentGame.getBoard());
    }

    private void leaveGame(String[] params) throws InputException, ResponseException {
        // leaves the game
    }

    private void makeMove(String[] params) throws InputException, ResponseException {
        // checks validity of move and makes move
    }

    private void resign(String[] params) throws InputException, ResponseException {
        // resigns from game
        // when the game ends, append an [OVER] to the end of the game name, so the client can tell which ones have been completed
    }

    private void highlight(String[] params) throws InputException {
        // highlights the requested piece's valid moves
    }
}
