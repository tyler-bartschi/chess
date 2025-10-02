package chess.movemanagers;

import chess.*;

import java.util.Collection;

public class KingMovement extends BaseMovementRule {
    private final int[][] steps = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}, {1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
    private final boolean recursive = false;

    public KingMovement() {

    }

    /**
     * Checks if a piece is a rook
     *
     * @param piece The ChessPiece to check
     * @return true if piece is a rook, false otherwise
     */
    private boolean checkIsRook(ChessPiece piece) {
        return piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK;
    }

    /**
     * Checks the spaces between the king and rook on the left
     *
     * @param board current ChessBoard
     * @param row   king's row
     * @return false if there is a piece between the rook and King, true otherwise
     */
    private boolean checkBetweenLeft(ChessBoard board, int row) {
        for (int i = 4; i > 1; i--) {
            if (board.getPiece(new ChessPosition(row, i)) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the spaces between the king and rook on the right
     *
     * @param board current ChessBoard
     * @param row   king's row
     * @return false if there is a piece between the rook and King, true otherwise
     */
    private boolean checkBetweenRight(ChessBoard board, int row) {
        for (int i = 6; i < 8; i++) {
            if (board.getPiece(new ChessPosition(row, i)) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a king is able to castle, and adds those castling moves to the possibleMoves Collection. Does not check if the castle is safe
     *
     * @param board         current board
     * @param myPosition    King's position
     * @param possibleMoves Collection of possible ChessMoves
     */
    private void checkCastling(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        ChessPiece myPiece = board.getPiece(myPosition);
        int startRow = myPiece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8;
        if (myPiece.getHasMoved() || myPosition.getRow() != startRow) {
            return;
        }

        ChessPosition firstRookPosition = new ChessPosition(startRow, 1);
        ChessPosition secondRookPosition = new ChessPosition(startRow, 8);
        ChessPiece firstRook = board.getPiece(firstRookPosition);
        ChessPiece secondRook = board.getPiece(secondRookPosition);

        if (checkIsRook(firstRook) && !firstRook.getHasMoved() && checkBetweenLeft(board, startRow)) {
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(startRow, 1), null));
        }

        if (checkIsRook(secondRook) && !secondRook.getHasMoved() && checkBetweenRight(board, startRow)) {
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(startRow, 8), null));
        }

    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = calculateMoves(board, myPosition, steps, recursive);
        checkCastling(board, myPosition, possibleMoves);
        return possibleMoves;
    }
}
