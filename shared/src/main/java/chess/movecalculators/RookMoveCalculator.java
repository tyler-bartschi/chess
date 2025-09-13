package chess.movecalculators;

import chess.*;

import java.util.Collection;

/**
 * Contains the mathematical rules for calculating a Rook's potential moves
 */
public class RookMoveCalculator {
    /**
     * Calculates all possible moves for a rook
     *
     * @param board      current chess board
     * @param myPosition Rook's current position
     * @return Collection of all possible ChessMoves for a Rook
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        return ChessMoveUtils.multiMoveCheck(board, myPosition, steps);
    }
}
