package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KingMovement extends BaseMovementRule {
    private final int[][] steps = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}, {1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
    private final boolean recursive = false;

    public KingMovement() {

    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return pieceMoves(board, myPosition, steps, recursive);
    }
}
