package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveUtils {
    public static Collection<ChessMove> singleMoveCheck(ChessBoard board, ChessPosition myPosition, final int[][] steps) {
        var possibleMoves = new ArrayList<ChessMove>();

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

    public static Collection<ChessMove> multiMoveCheck(ChessBoard board, ChessPosition myPosition, final int[][] steps) {
        var possibleMoves = new ArrayList<ChessMove>();
        for (int[] step : steps) {
            int rowAdder = step[0];
            int colAdder = step[1];
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
