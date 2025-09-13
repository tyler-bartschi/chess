package chess;

import java.util.Collection;
import java.util.ArrayList;

public abstract class PieceMoveCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        var possibleMoves = new ArrayList<ChessMove>();

        if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return possibleMoves;
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return possibleMoves;
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return possibleMoves;
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return possibleMoves;
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return possibleMoves;
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.KING) {
            return possibleMoves;
        } else {
            return possibleMoves;
        }
    }
}
