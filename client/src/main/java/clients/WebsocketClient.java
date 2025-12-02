package clients;

import chess.*;
import facades.WebsocketException;
import facades.WebsocketFacade;
import ui.InputException;
import ui.UI.UICommand;
import ui.BoardRenderer;
import static utils.ClientUtils.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

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
            System.out.println(message);
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
        checkMoveValidity(move);

        websocketFacade.sendMakeMoveCommand(move);
    }

    private void resign(String[] params) throws InputException, WebsocketException {
        if (params.length != 0) {
            throw new InputException("Too many parameters provided. 'resign' takes no parameters");
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("WARNING: By resigning, you will lost the game. Are you sure you want to resign? (y/n)");
            String line = scanner.nextLine();

            if (isYes(line)) {
                running = false;
            } else if (isNo(line)) {
                System.out.println("Canceling resignation...");
                return;
            } else {
                System.out.println(line + " is not recognized. Please enter 'y' or 'n'");
            }
        }

        websocketFacade.sendResignCommand();
    }

    private void highlight(String[] params) throws InputException {
        // highlights the requested piece's valid moves
    }

    private void checkMoveValidity(ChessMove move) throws InputException {
        ChessGame.TeamColor currentColor = currentGame.getTeamTurn();
        if (teamColor != currentColor) {
            throw new InputException("It is not your turn, cannot make a move");
        }

        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> possibleMoves = currentGame.validMoves(startPosition);
        if (!possibleMoves.contains(move)) {
            throw new InputException("That is not a valid move. Please try again.");
        }
    }


    private boolean isYes(String line) {
        return line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes");
    }

    private boolean isNo(String line) {
        return line.equalsIgnoreCase("n") || line.equalsIgnoreCase("no");
    }

    private ChessMove parseMove(String[] params) throws InputException {
        ChessMove desiredMove = null;

        String startPositionRaw = params[0];
        String endPositionRaw = params[0];

        int startColumnNum = letterToNum(startPositionRaw.substring(1));
        int endColumnNum = letterToNum(endPositionRaw.substring(1));
        checkBoundary(startColumnNum);
        checkBoundary(endColumnNum);

        String startRowRaw = startPositionRaw.substring(0, 1);
        String endRowRaw  = startPositionRaw.substring(0, 1);
        if (startRowRaw.matches("\\d+") && endRowRaw.matches("\\d+")) {
            int startRowNum = Integer.parseInt(startRowRaw);
            int endRowNum = Integer.parseInt(endRowRaw);
            checkBoundary(startRowNum);
            checkBoundary(endRowNum);
            ChessPosition startPosition = new ChessPosition(startRowNum, startColumnNum);
            ChessPosition endPosition = new ChessPosition(endRowNum, endColumnNum);
            if (isPawn(startPosition)) {
                ChessPiece.PieceType promotionPiece = getPromotionPiece(startPosition);
                desiredMove = new ChessMove(startPosition, endPosition, promotionPiece);
            } else {
                desiredMove = new ChessMove(startPosition, endPosition, null);
            }

        } else {
            throw new InputException("One of your rows is not a valid number");
        }
        return desiredMove;
    }

    private ChessPiece.PieceType getPromotionPiece(ChessPosition startPosition) {
        boolean isPromoting = false;
        if (teamColor == ChessGame.TeamColor.WHITE && startPosition.getRow() == 7) {
            isPromoting = true;
        } else if (startPosition.getRow() == 2) {
            isPromoting = true;
        }

        if (isPromoting) {
            return promptForPiece();
        }
        return null;
    }

    private ChessPiece.PieceType promptForPiece() {
        System.out.println("Your pawn is going to be promoted! Please enter your desired promotion piece.");
        ChessPiece.PieceType pieceType = null;

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            String line = scanner.nextLine();
            pieceType = convertStringToPieceType(line);
            if (pieceType != null) {
                running = false;
            }
            System.out.println("That is not a valid piece type. Please enter: ROOK/KNIGHT/BISHOP/QUEEN");
        }
        return pieceType;
    }

    private ChessPiece.PieceType convertStringToPieceType(String line) {
        line = line.toUpperCase();

        return switch (line) {
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            default -> null;
        };
    }

    private boolean isPawn(ChessPosition position) {
        ChessPiece piece = currentGame.getBoard().getPiece(position);
        return piece.getPieceType() == ChessPiece.PieceType.PAWN;
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
