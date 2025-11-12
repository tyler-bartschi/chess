package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardRenderer {

    public BoardRenderer() {

    }

    public void renderGameBoard(ChessGame.TeamColor team, ChessBoard board) {
        // white on bottom, black on top by default
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};
        if (team == ChessGame.TeamColor.BLACK) {
            letters = reverseStringArray(letters);
            numbers = reverseStringArray(numbers);
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
