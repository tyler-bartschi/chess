package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();
        final int[][] steps = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int i = 0; i < steps.length; i++) {
            int rowAdder = steps[i][0];
            int colAdder = steps[i][1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            while (row <= 8 && col <= 8 && row > 0 && col > 0) {
                if (ChessBoard.checkFriendlyPresence(board, new ChessPosition(row, col), myPosition)) {
                    break;
                }
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                if (ChessBoard.checkEnemyPresence(board, new ChessPosition(row, col), myPosition)) {
                    break;
                }
                row += rowAdder;
                col += colAdder;
            }
        }

        return possibleMoves;
    }
}
