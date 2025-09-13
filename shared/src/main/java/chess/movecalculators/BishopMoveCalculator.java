package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();
        final int[][] steps = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int i = 0; i < steps.length; i++) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            int rowAdder = steps[i][0];
            int colAdder = steps[i][1];

            while (row < 8 && col < 8) {
                row += rowAdder;
                col += colAdder;
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        return possibleMoves;
    }

}
