package facades;

import chess.*;
import ui.BoardRenderer;
import ui.InputException;

public class ServerFacade {

    private final BoardRenderer boardRenderer;
    private final String serverUrl;
    private String authToken;
    private String username;
    // add a table keeping track of numbers and the corresponding games when list games is called


    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
        boardRenderer = new BoardRenderer();
    }

    public String login(String[] params) throws InputException, ResponseException {
        return "";
    }

    public String register(String[] params) throws InputException, ResponseException {
        return "";
    }

    public String logout(String[] params) throws ResponseException {
        // invalidate all fields
        return "";
    }

    public String create(String[] params) throws InputException, ResponseException {
        return "";
    }

    public String list(String[] params) throws ResponseException {
        return "";
    }

    public String join (String[] params) throws InputException, ResponseException {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return boardRenderer.renderGameBoard(ChessGame.TeamColor.WHITE, board);
    }

    public String observe(String[] params) throws InputException, ResponseException {
        return "";
    }
}
