package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        final int[][] steps = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        return ChessMoveUtils.multiMoveCheck(board, myPosition, steps);
    }
}
