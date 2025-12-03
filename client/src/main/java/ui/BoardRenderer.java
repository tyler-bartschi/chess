package ui;

import chess.*;

import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class BoardRenderer {

    private StringBuilder result;

    public BoardRenderer() {

    }

    public String renderGameBoard(ChessGame.TeamColor team, ChessBoard board, ArrayList<int[]> highlights) {
        result = new StringBuilder();
        // white on bottom, black on top by default
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};
        if (team == ChessGame.TeamColor.BLACK) {
            letters = reverseStringArray(letters);
            numbers = reverseStringArray(numbers);
        }

        addLetterPositions(letters);
        buildGameBoard(numbers, board, team, highlights);
        addLetterPositions(letters);
        return result.toString();
    }

    private void addLetterPositions(String[] letters) {
        result.append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK).append(EMPTY);
        for (String letter : letters) {
            result.append(" ").append(letter).append(" ");
        }
        result.append(EMPTY).append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
    }

    private void buildGameBoard(String[] numbers, ChessBoard board, ChessGame.TeamColor team, ArrayList<int[]> highlights) {
        for (String number : numbers) {
            buildOneLine(number, board, team, highlights);
        }
    }

    private void buildOneLine(String number, ChessBoard board, ChessGame.TeamColor team, ArrayList<int[]> highlights) {
        int num = Integer.parseInt(number);
        boolean isWhite = (team == ChessGame.TeamColor.WHITE) == (num % 2 == 0);
        String printableNum = " " + number + " ";
        result.append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK).append(printableNum).append(RESET_TEXT_COLOR);
        if (team == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i < 9; i++) {
                isWhite = insertIntoLine(i, board, num, highlights, isWhite);
            }
        } else {
            for (int i = 8; i >= 1; i--) {
                isWhite = insertIntoLine(i, board, num, highlights, isWhite);
            }
        }

        result.append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK).append(printableNum)
                .append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
    }

    private boolean insertIntoLine(int i, ChessBoard board, int num, ArrayList<int[]> highlights, boolean isWhite) {
        String bgColor;
        if (highlights.contains(new int[]{num, i})) {
            bgColor = isWhite ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
        } else {
            bgColor = isWhite ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_BLACK;
        }
        result.append(bgColor).append(getPieceForPosition(num, i, board));
        return !isWhite;
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
