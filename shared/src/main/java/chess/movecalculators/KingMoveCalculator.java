package chess.movecalculators;

import chess.*;

import java.util.Collection;

public class KingMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
        return ChessMoveUtils.singleMoveCheck(board, myPosition, steps);
    }
}
