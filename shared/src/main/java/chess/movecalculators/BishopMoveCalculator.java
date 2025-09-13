package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator {
    private static boolean checkEnemyPresence(ChessBoard board, ChessPosition currentPosition, ChessPosition myPosition) {
        ChessPiece occupant = board.getPiece(currentPosition);
        ChessPiece myPiece = board.getPiece(myPosition);
        return occupant != null && occupant.getTeamColor() != myPiece.getTeamColor();
    }

    private static boolean checkFriendlyPresence(ChessBoard board, ChessPosition currentPosition, ChessPosition myPosition) {
        ChessPiece occupant = board.getPiece(currentPosition);
        ChessPiece myPiece = board.getPiece(myPosition);
        return occupant != null && occupant.getTeamColor() == myPiece.getTeamColor();
    }

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();
        final int[][] steps = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int i = 0; i < steps.length; i++) {
            int rowAdder = steps[i][0];
            int colAdder = steps[i][1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            while (row <= 8 && col <= 8 && row > 0 && col > 0) {
                if (checkFriendlyPresence(board, new ChessPosition(row, col), myPosition)) {
                    break;
                }
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                if (checkEnemyPresence(board, new ChessPosition(row, col), myPosition)) {
                    break;
                }
                row += rowAdder;
                col += colAdder;
            }
        }

        return possibleMoves;
    }

}
