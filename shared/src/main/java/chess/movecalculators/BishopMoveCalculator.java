package chess.movecalculators;

import chess.*;

import java.util.Collection;

/**
 * Calculates all the moves a Bishop could take
 */
public class BishopMoveCalculator {
    /**
     * Contains mathematical rules of bishops, calculates a bishops' possible moveset
     *
     * @param board      current chess board
     * @param myPosition current position of Bishop
     * @return Collection of all possible ChessMoves the bishop can take
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        return ChessMoveUtils.multiMoveCheck(board, myPosition, steps);
    }
}
