package chess.movecalculators;

import chess.*;

import java.util.Collection;

/**
 * Calculates the possible moves in the Knight's moveset
 */
public class KnightMoveCalculator {
    /**
     * Contains the mathematical rules for determining the Knight's possible moves
     *
     * @param board      current chess board
     * @param myPosition Knight's position
     * @return Collection of all possible Knight ChessMoves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}};
        return ChessMoveUtils.singleMoveCheck(board, myPosition, steps);
    }
}
