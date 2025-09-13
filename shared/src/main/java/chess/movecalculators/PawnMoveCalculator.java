package chess.movecalculators;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Calculates possible moves for a pawn given a position
 */
public class PawnMoveCalculator {
    /**
     * Generates an array of int[] that mathematically determines where the pawn can move
     *
     * @param myPosition current position of the piece
     * @param myColor    color of the piece
     * @return an array of int[] that holds the pawn's moveset
     */
    private static int[][] generateSteps(ChessPosition myPosition, ChessGame.TeamColor myColor) {
        int[][] steps = new int[4][2];

        if (myColor == ChessGame.TeamColor.WHITE) {
            steps[0] = new int[]{1, 0};
            if (myPosition.getRow() == 2) {
                steps[1] = new int[]{2, 0};
            }
            steps[2] = new int[]{1, -1};
            steps[3] = new int[]{1, 1};
        } else {
            steps[0] = new int[]{-1, 0};
            if (myPosition.getRow() == 7) {
                steps[1] = new int[]{-2, 0};
            }
            steps[2] = new int[]{-1, -1};
            steps[3] = new int[]{-1, 1};
        }

        return steps;
    }

    /**
     * Checks both the square in front of and the second square in front of the pawn, determining if the pawn can move 2 spaces or not
     *
     * @param board           current ChessBoard
     * @param currentPosition Position of the second square on a pawn's first move
     * @param myPosition      Position of pawn
     * @param myColor         color of pawn
     * @return true if it cannot move two squares, false if it can
     */
    private static boolean checkPawnPositions(ChessBoard board, ChessPosition currentPosition, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        ChessPosition lowerPosition;
        if (myColor == ChessGame.TeamColor.WHITE) {
            lowerPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
        } else {
            lowerPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());

        }
        return ChessBoard.checkFriendlyPresence(board, currentPosition, myPosition) || ChessBoard.checkFriendlyPresence(board, lowerPosition, myPosition)
                || ChessBoard.checkEnemyPresence(board, currentPosition, myPosition) || ChessBoard.checkEnemyPresence(board, lowerPosition, myPosition);
    }

    /**
     * Adds all the possible promotion moves for a pawn
     *
     * @param myPosition      pawn's position
     * @param currentPosition position pawn will move into
     * @param possibleMoves   ArrayList of ChessMoves that represent what moves the pawn can take
     */
    private static void addPromotionMoves(ChessPosition myPosition, ChessPosition currentPosition, ArrayList<ChessMove> possibleMoves) {
        possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.ROOK));
        possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.BISHOP));
        possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.KNIGHT));
        possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.QUEEN));
    }

    /**
     * Calculates all potential moves a pawn can take
     *
     * @param board      current board
     * @param myPosition pawn's position
     * @return Collection of all possible ChessMoves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        final int[][] steps = generateSteps(myPosition, myColor);
        final int[] invalidStep = {0, 0};

        for (int i = 0; i < steps.length; i++) {
            int[] step = steps[i];
            int rowAdder = step[0];
            int colAdder = step[1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            if (row > 8 || row < 1 || col > 8 || col < 1) {
                continue;
            }

            if (i == 0) {
                if (ChessBoard.checkFriendlyPresence(board, new ChessPosition(row, col), myPosition) || ChessBoard.checkEnemyPresence(board, new ChessPosition(row, col), myPosition)) {
                    continue;
                } else {
                    if (row == 8 || row == 1) {
                        addPromotionMoves(myPosition, new ChessPosition(row, col), possibleMoves);
                    } else {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                }
            } else if (i == 1) {
                if (Arrays.equals(step, invalidStep) || checkPawnPositions(board, new ChessPosition(row, col), myPosition, myColor)) {
                    continue;
                } else {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            } else {
                if (ChessBoard.checkEnemyPresence(board, new ChessPosition(row, col), myPosition)) {
                    if (row == 8 || row == 1) {
                        addPromotionMoves(myPosition, new ChessPosition(row, col), possibleMoves);
                    } else {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                }
            }
        }
        return possibleMoves;
    }
}