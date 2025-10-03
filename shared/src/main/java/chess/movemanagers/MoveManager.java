package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class MoveManager implements MovementRule {
    private final MovementRule rook = new RookMovement();
    private final MovementRule knight = new KnightMovement();
    private final MovementRule bishop = new BishopMovement();
    private final MovementRule queen = new QueenMovement();
    private final MovementRule king = new KingMovement();
    private final MovementRule pawn = new PawnMovement();

    public MoveManager() {

    }

    /**
     * Checks whether the move is a castle move or not
     *
     * @param move the desired ChessMove
     * @return true if it is a castling move, false otherwise
     */
    public boolean kingWantsCastle(ChessMove move) {
        int startColumn = move.getStartPosition().getColumn();
        int endColumn = move.getEndPosition().getColumn();
        return endColumn == startColumn + 2 || endColumn == startColumn - 2;
    }

    /**
     * Checks if a pawn's move will be EnPassant or not
     *
     * @param move the move being made
     * @return true if an enPassant move, false otherwise
     */
    public boolean pawnWantsEnPassant(ChessBoard board, ChessMove move) {
        return board.getPiece(move.getEndPosition()) == null &&
                move.getEndPosition().getRow() != 8 && move.getEndPosition().getRow() != 1;
    }

    /**
     * Determines what type the current piece is and calls a MovementRule accordingly
     *
     * @param board      current ChessBoard
     * @param myPosition current position of piece
     * @return Collection of ChessMoves that is the possible moveset for the piece
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType myPiece = board.getPiece(myPosition).getPieceType();
        Collection<ChessMove> possibleMoves;

        possibleMoves = switch (myPiece) {
            case ChessPiece.PieceType.ROOK -> rook.pieceMoves(board, myPosition);
            case ChessPiece.PieceType.KNIGHT -> knight.pieceMoves(board, myPosition);
            case ChessPiece.PieceType.BISHOP -> bishop.pieceMoves(board, myPosition);
            case ChessPiece.PieceType.QUEEN -> queen.pieceMoves(board, myPosition);
            case ChessPiece.PieceType.KING -> king.pieceMoves(board, myPosition);
            case ChessPiece.PieceType.PAWN -> pawn.pieceMoves(board, myPosition);
            default -> new ArrayList<ChessMove>();
        };

        return possibleMoves;
    }
}
