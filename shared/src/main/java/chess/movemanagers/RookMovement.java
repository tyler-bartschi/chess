package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class RookMovement extends BaseMovementRule {
    private final int[][] steps = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
    private final boolean recursive = true;

    public RookMovement() {

    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return calculateMoves(board, myPosition, steps, recursive);
    }
}
