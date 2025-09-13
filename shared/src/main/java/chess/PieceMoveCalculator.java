package chess;

import chess.movecalculators.*;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Calculates the moveset for any given piece
 */
public abstract class PieceMoveCalculator {
    /**
     * Calculates the moveset for any given piece
     *
     * @param board      current ChessBoard
     * @param myPosition current piece position
     * @return Collection of ChessMoves representing that piece's possible moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        var possibleMoves = new ArrayList<ChessMove>();

        if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return PawnMoveCalculator.pieceMoves(board, myPosition);
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return BishopMoveCalculator.pieceMoves(board, myPosition);
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return RookMoveCalculator.pieceMoves(board, myPosition);
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return KnightMoveCalculator.pieceMoves(board, myPosition);
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return QueenMoveCalculator.pieceMoves(board, myPosition);
        } else if (myPiece.getPieceType() == ChessPiece.PieceType.KING) {
            return KingMoveCalculator.pieceMoves(board, myPosition);
        } else {
            return possibleMoves;
        }
    }
}
