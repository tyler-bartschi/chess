package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class BishopMovement extends BaseMovementRule {
    private final int[][] steps = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
    private final boolean recursive = true;

    public BishopMovement() {

    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return calculateMoves(board, myPosition, steps, recursive);
    }
}
