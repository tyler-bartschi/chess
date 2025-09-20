package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KnightMovement extends BaseMovementRule {
    private final int[][] steps = {{2, -1}, {2, 1}, {-2, -1}, {-2, 1}, {1, -2}, {-1, -2}, {1, 2}, {-1, 2}};
    private final boolean recursive = false;

    public KnightMovement() {

    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return pieceMoves(board, myPosition, steps, recursive);
    }
}
