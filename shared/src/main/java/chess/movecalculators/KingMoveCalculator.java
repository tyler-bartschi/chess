package chess.movecalculators;

import chess.*;

import java.util.Collection;

/**
 * Contains the logic of how a King can move
 */
public class KingMoveCalculator {
    /**
     * Calculates all of a King's possible moves
     *
     * @param board      current chess board
     * @param myPosition current position of King
     * @return Collection of all possible ChessMoves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
        return ChessMoveUtils.singleMoveCheck(board, myPosition, steps);
    }
}
