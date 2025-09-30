package chess;

import java.util.Collection;

public class ValidMoveFilter {
    public ValidMoveFilter() {

    }

    /**
     * Given a board and the king's position, returns if the King is in check
     *
     * @param board        current chessboard
     * @param kingPosition king's position
     * @return true if the king is in check, false otherwise
     */
    public boolean checkIfInCheck(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor kingColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece == null || currentPiece.getTeamColor() == kingColor) {
                    continue;
                }
                Collection<ChessMove> currentMoves = currentPiece.pieceMoves(board, currentPosition);
                for (ChessMove move : currentMoves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
