package ui;

import chess.*;
import facades.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UI {

    private AuthState state;
    private final ServerFacade serverFacade;

    private enum AuthState {
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    public UI(int port) {
        state = AuthState.UNAUTHENTICATED;
        serverFacade = new ServerFacade(port);
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to chess! Type 'help' to get started. " + BLACK_KING);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                running = evaluate(line);
            } catch (InputException ex) {
                printErrorMessage(ex.getMessage());
            } catch (Throwable ex) {
                printErrorMessage("An unidentified error occurred. Please try again.");
            }
        }

        System.out.println("Thanks for playing!");
    }

    private void printPrompt() {
        resetTextEffects();
        if (state == AuthState.UNAUTHENTICATED) {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_OUT] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        } else {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_IN] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        }
    }

    private void resetTextEffects() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_UNDERLINE + RESET_TEXT_ITALIC + RESET_TEXT_BLINKING + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private boolean evaluate(String line) throws InputException {
        resetTextEffects();
        String[] tokens = line.toLowerCase().split("\\s+");
        if (tokens[0].isEmpty()) {
            throw new InputException("No input provided, please type a command.");
        }
        String cmd = tokens[0];
        String[] rawParams = Arrays.copyOfRange(line.split("\\s+"), 1, tokens.length);
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "help" -> help();
            case "login" -> login(rawParams);
            case "register" -> register(rawParams);
            case "logout" -> logout();
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> false;
            default ->
                    throw new InputException("'" + cmd + "'" + " is not a recognized command. Run 'help' to see a list of available commands.");
        };
    }

    private void printErrorMessage(String msg) {
        resetTextEffects();
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + msg);
    }

    private boolean help() {
        String caseSensitive = SET_TEXT_COLOR_MAGENTA + "NOTE: fields surrounded by <> are " + SET_TEXT_BOLD + "case sensitive" + RESET_TEXT_BOLD_FAINT;
        if (state == AuthState.UNAUTHENTICATED) {
            printBlueAndWhite("register <USERNAME> <PASSWORD> <EMAIL> ", "- to create an account " + caseSensitive);
            printBlueAndWhite("login <USERNAME> <PASSWORD> ", "- login to play chess " + caseSensitive);
            printBlueAndWhite("quit ", "- quits the chess program");
            printBlueAndWhite("help", " - display all possible commands");
        }

        if (state == AuthState.AUTHENTICATED) {
            printBlueAndWhite("create <NAME> ", "- create a game, NAME will be made all lower case");
            printBlueAndWhite("list ", "- list all games");
            printBlueAndWhite("join <ID> [WHITE|BLACK] ", "- join a game");
            printBlueAndWhite("observe <ID> ", "- observe a game");
            printBlueAndWhite("logout ", "- logs out of the chess program");
            printBlueAndWhite("quit ", "- quits the chess program");
            printBlueAndWhite("help ", "- display all possible commands");
        }
        System.out.println();
        return true;
    }

    private boolean login(String[] params) throws InputException {
        throwIfAuthenticated("You are already logged in.");
        return true;
    }

    private boolean register(String[] params) throws InputException {
        throwIfAuthenticated("You cannot register while logged in.");
        state = AuthState.AUTHENTICATED;
        return true;
    }

    private boolean logout() throws InputException {
        throwIfUnauthenticated("You are already logged out.");
        state = AuthState.UNAUTHENTICATED;
        return true;
    }

    private boolean create(String[] params) throws InputException {
        throwIfUnauthenticated("Must be logged in to create a game.");
        return true;
    }

    private boolean list() throws InputException {
        throwIfUnauthenticated("Must be logged in to list games.");
        return true;
    }

    private boolean join(String[] params) throws InputException {
        throwIfUnauthenticated("Must be logged in to join a game");
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println("WHITE CHESSBOARD");
        renderGameBoard(ChessGame.TeamColor.WHITE, board);
        System.out.println("\nBLACK CHESSBOARD");
        renderGameBoard(ChessGame.TeamColor.BLACK, board);
        return true;
    }

    private boolean observe(String[] params) throws InputException {
        throwIfUnauthenticated("Must be logged in to observe a game.");
        return true;
    }

    private void throwIfUnauthenticated(String message) throws InputException {
        if (state == AuthState.UNAUTHENTICATED) {
            throw new InputException(message);
        }
    }

    private void throwIfAuthenticated(String message) throws InputException {
        if (state == AuthState.AUTHENTICATED) {
            throw new InputException(message);
        }
    }

    private void printBlueAndWhite(String first, String second) {
        System.out.println(EMPTY + SET_TEXT_COLOR_BLUE + first + SET_TEXT_COLOR_WHITE + second);
    }

    private void renderGameBoard(ChessGame.TeamColor team, ChessBoard board) {
        resetTextEffects();
        // white on bottom, black on top by default
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};
        if (team == ChessGame.TeamColor.BLACK) {
            letters = reverseStringArray(letters);
            numbers = reverseStringArray(numbers);
            board = board.invertBoard();
        }

        printLetterPositions(letters);
        printGameBoard(numbers, board, team);
        printLetterPositions(letters);

    }

    private void printLetterPositions(String[] letters) {
        System.out.print(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
        for (String letter : letters) {
            System.out.print(" " + letter + " ");
        }

        System.out.println(EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void printGameBoard(String[] numbers, ChessBoard board, ChessGame.TeamColor team) {
        for (String number : numbers) {
            printOneLine(number, board, team);
        }
    }

    private void printOneLine(String number, ChessBoard board, ChessGame.TeamColor team) {
        int num = Integer.parseInt(number);
        boolean isWhite = (team == ChessGame.TeamColor.WHITE) == (num % 2 == 0);
        String printableNum = " " + number + " ";
        System.out.print(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + printableNum + RESET_TEXT_COLOR);
        for (int i = 1; i < 9; i++) {
            String bgColor = isWhite ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_BLACK;
            System.out.print(bgColor + getPieceForPosition(num, i, board));
            isWhite = !isWhite;
        }
        System.out.println(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + printableNum + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private String getPieceForPosition(int row, int col, ChessBoard board) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return EMPTY;
        }
        ChessPiece.PieceType pieceType = piece.getPieceType();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return getPieceString(pieceType, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN);
        }
        return getPieceString(pieceType, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN);

    }

    private String getPieceString(ChessPiece.PieceType pieceType, String king, String queen, String bishop, String knight, String rook, String pawn) {
        return switch (pieceType) {
            case ChessPiece.PieceType.KING -> king;
            case ChessPiece.PieceType.QUEEN -> queen;
            case ChessPiece.PieceType.BISHOP -> bishop;
            case ChessPiece.PieceType.KNIGHT -> knight;
            case ChessPiece.PieceType.ROOK -> rook;
            case ChessPiece.PieceType.PAWN -> pawn;
            default -> EMPTY;
        };
    }

    private String[] reverseStringArray(String[] original) {
        String[] newArray = new String[original.length];
        for (int i = 0; i < original.length; i++) {
            int newIndex = original.length - i - 1;
            newArray[newIndex] = original[i];
        }

        return newArray;
    }
}
