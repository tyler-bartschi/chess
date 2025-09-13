package chess.movecalculators;

import chess.*;

import java.util.Collection;

/**
 * Calculates the Queen's possible moveset
 */
public class QueenMoveCalculator {
    /**
     * Contains the mathematical parameters of how to compute the Queen's moveset
     *
     * @param board      current chess board
     * @param myPosition position of the Queen
     * @return Collection of all possible Queen ChessMoves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        return ChessMoveUtils.multiMoveCheck(board, myPosition, steps);
    }
}
