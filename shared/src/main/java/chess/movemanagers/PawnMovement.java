package chess.movemanagers;

import chess.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PawnMovement implements MovementRule {
    private final int[] INVALID_STEP = {0, 0};

    public PawnMovement() {

    }

    /**
     * Determines if a position is within bounds
     *
     * @param row row number
     * @param col column number
     * @return True if within bounds, false otherwise
     */
    private boolean checkBounds(int row, int col) {
        return row <= 8 && row > 0 && col <= 8 && col > 0;
    }

    /**
     * Checks if a pawn can be promoted or not, and adds the appropriate moves
     *
     * @param myPosition      position of pawn
     * @param currentPosition position pawn can move into
     * @param possibleMoves   Collection of ChessMoves representing the possible moveset of the pawn
     */
    private void checkPromotion(ChessPosition myPosition, ChessPosition currentPosition, Collection<ChessMove> possibleMoves) {
        if (currentPosition.getRow() == 8 || currentPosition.getRow() == 1) {
            possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.BISHOP));
            possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.ROOK));
            possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.KNIGHT));
            possibleMoves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.QUEEN));
        } else {
            possibleMoves.add(new ChessMove(myPosition, currentPosition, null));
        }
    }

    /**
     * When a pawn can move 2 spaces, checks if both that space and the previous space are clear
     *
     * @param board           current chess board
     * @param currentPosition position of pawn
     * @param myColor         color of pawn
     * @return True if a piece is in one of the potential spaces, false otherwise
     */
    private boolean checkManyPositions(ChessBoard board, ChessPosition currentPosition, ChessGame.TeamColor myColor) {
        int offset = myColor == ChessGame.TeamColor.WHITE ? -1 : 1;
        ChessPosition lowerPosition = new ChessPosition(currentPosition.getRow() + offset, currentPosition.getColumn());

        return board.checkFriendlyPosition(currentPosition, myColor) || board.checkFriendlyPosition(lowerPosition, myColor) ||
                board.checkEnemyPosition(currentPosition, myColor) || board.checkEnemyPosition(currentPosition, myColor);

    }

    /**
     * Generates a collection of ChessMoves that the pawn can take
     *
     * @param board      current chess board
     * @param myPosition position of pawn
     * @param myColor    color of pawn
     * @param steps      int[][] of steps the pawn could take
     * @return Collection of ChessMoves representing the legal moves the pawn could take
     */
    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor, final int[][] steps) {
        var possibleMoves = new ArrayList<ChessMove>();

        for (int i = 0; i < 4; i++) {
            int[] step = steps[i];
            int rowAdder = step[0];
            int colAdder = step[1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            var currentPosition = new ChessPosition(row, col);


            if (Arrays.equals(step, INVALID_STEP) || !checkBounds(row, col) || board.checkFriendlyPosition(currentPosition, myColor)) {
                continue;
            }

            if ((i == 2 || i == 3) && board.checkEnemyPosition(currentPosition, myColor)) {
                checkPromotion(myPosition, currentPosition, possibleMoves);
            } else if (i == 1 && !checkManyPositions(board, currentPosition, myColor)) {
                possibleMoves.add(new ChessMove(myPosition, currentPosition, null));
            } else if (i == 0 && !board.checkEnemyPosition(currentPosition, myColor)) {
                checkPromotion(myPosition, currentPosition, possibleMoves);
            }
        }

        return possibleMoves;
    }

    /**
     * Generates the steps array, depending on piece color and position
     *
     * @param myColor    piece color
     * @param myPosition piece position
     * @return int[][] of potential steps a pawn could take
     */
    private int[][] generateSteps(ChessGame.TeamColor myColor, ChessPosition myPosition) {
        int[][] steps = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};

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

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        final int[][] steps = generateSteps(myColor, myPosition);

        return calculateMoves(board, myPosition, myColor, steps);
    }
}
