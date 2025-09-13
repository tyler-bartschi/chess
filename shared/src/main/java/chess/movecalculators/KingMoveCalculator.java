package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();
        final int[][] steps = {{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

        for (int[] step : steps) {
            int rowAdder = step[0];
            int colAdder = step[1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                if (!ChessBoard.checkFriendlyPresence(board, new ChessPosition(row, col), myPosition)) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            }
        }
        return possibleMoves;
    }
}
