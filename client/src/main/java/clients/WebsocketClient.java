package clients;

import chess.ChessGame;
import chess.ChessMove;
import facades.ResponseException;
import facades.WebsocketException;
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
            System.out.println("Echoing: " + message);
        }

    }

    public UICommand execute(String[] tokens) throws InputException, WebsocketException {
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

    public void activate(String authToken, int gameID) throws WebsocketException {
        // activate the websocketFacade
        websocketFacade.setServerMessageObserver(new ServerMessageObserver());
        websocketFacade.setAuthToken(authToken);
        websocketFacade.setGameID(gameID);
        websocketFacade.createConnection();
        websocketFacade.sendConnectCommand();
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

    private void leaveGame(String[] params) throws InputException, WebsocketException {
        if (params.length != 0) {
            throw new InputException("Too many parameters provided. 'leave' takes no parameters");
        }
        websocketFacade.sendLeaveCommand();
    }

    private void makeMove(String[] params) throws InputException, WebsocketException {
        if (params.length != 2) {
            throw new InputException("'move' requires exactly two parameters. <StartRow><StartColumn> and <EndRow><EndColumn>");
        }

        ChessMove move = parseMove(params);
    }

    private void resign(String[] params) throws InputException, WebsocketException {
        if (params.length != 0) {
            throw new InputException("Too many parameters provided. 'resign' takes no parameters");
        }
        // REQUIRES CONFIRMATION !!
        websocketFacade.sendResignCommand();
    }

    private void highlight(String[] params) throws InputException {
        // highlights the requested piece's valid moves
    }

    private ChessMove parseMove(String[] params) throws InputException {
        String startPositionRaw = params[0];
        String endPositionRaw = params[0];

        int startColumnNum = letterToNum(startPositionRaw.substring(1));
        int endColumnNum = letterToNum(endPositionRaw.substring(1));

        String startRowRaw = startPositionRaw.substring(0, 1);
        String endRowRaw  = startPositionRaw.substring(0, 1);
        if (startRowRaw.matches("\\d+") && endRowRaw.matches("\\d+")) {
            int startRowNum = Integer.parseInt(startRowRaw);
            int endRowNum = Integer.parseInt(endRowRaw);
            checkBoundary(startRowNum);
            checkBoundary(endRowNum);
            // gonna need to do a couple things: check if it's a pawn, and if it is it needs to be able to promote.
            // either prompt for promotion piece or include it as part of the command?
        } else {
            throw new InputException("One of your rows is not a valid number");
        }
    }

    private void checkBoundary(int num) throws InputException {
        if (num <= 0 || num > 8) {
            throw new InputException(num + " is out of bounds. Must be 1-8");
        }
    }

    private int letterToNum(String letter) throws InputException {
        return switch (letter) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new InputException(letter + " is not a valid column");
        };
    }
}
