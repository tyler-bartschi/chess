package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}};
        return ChessMoveUtils.singleMoveCheck(board, myPosition, steps);
    }
}
