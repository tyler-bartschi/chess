package chess.movecalculators;

import chess.*;

import java.util.Collection;

public class RookMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        return ChessMoveUtils.multiMoveCheck(board, myPosition, steps);
    }
}
