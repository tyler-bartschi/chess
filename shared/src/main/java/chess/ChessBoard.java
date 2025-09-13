package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Checks if a square is occupied by an enemy piece
     *
     * @param board           current ChessBoard
     * @param currentPosition position to check for enemy piece
     * @param myPosition      position of your piece
     * @return true if enemy piece present, false otherwise
     */
    public static boolean checkEnemyPresence(ChessBoard board, ChessPosition currentPosition, ChessPosition myPosition) {
        ChessPiece occupant = board.getPiece(currentPosition);
        ChessPiece myPiece = board.getPiece(myPosition);
        return occupant != null && occupant.getTeamColor() != myPiece.getTeamColor();
    }

    /**
     * Checks if a square is occupied by a friendly piece
     *
     * @param board           current ChessBoard
     * @param currentPosition position to check for friendly piece
     * @param myPosition      position of your piece
     * @return true if friendly piece present, false otherwise
     */
    public static boolean checkFriendlyPresence(ChessBoard board, ChessPosition currentPosition, ChessPosition myPosition) {
        ChessPiece occupant = board.getPiece(currentPosition);
        ChessPiece myPiece = board.getPiece(myPosition);
        return occupant != null && occupant.getTeamColor() == myPiece.getTeamColor();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // add all the white pieces
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessPiece rook = new ChessPiece(white, ChessPiece.PieceType.ROOK);
        ChessPiece knight = new ChessPiece(white, ChessPiece.PieceType.KNIGHT);
        ChessPiece bishop = new ChessPiece(white, ChessPiece.PieceType.BISHOP);
        ChessPiece pawn = new ChessPiece(white, ChessPiece.PieceType.PAWN);

        addPiece(new ChessPosition(1, 1), rook);
        addPiece(new ChessPosition(1, 2), knight);
        addPiece(new ChessPosition(1, 3), bishop);
        addPiece(new ChessPosition(1, 4), new ChessPiece(white, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(white, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), bishop);
        addPiece(new ChessPosition(1, 7), knight);
        addPiece(new ChessPosition(1, 8), rook);

        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(2, i), pawn);
        }

        // add all the black pieces
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
        rook = new ChessPiece(black, ChessPiece.PieceType.ROOK);
        knight = new ChessPiece(black, ChessPiece.PieceType.KNIGHT);
        bishop = new ChessPiece(black, ChessPiece.PieceType.BISHOP);
        pawn = new ChessPiece(black, ChessPiece.PieceType.PAWN);

        addPiece(new ChessPosition(8, 1), rook);
        addPiece(new ChessPosition(8, 2), knight);
        addPiece(new ChessPosition(8, 3), bishop);
        addPiece(new ChessPosition(8, 4), new ChessPiece(black, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(black, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), bishop);
        addPiece(new ChessPosition(8, 7), knight);
        addPiece(new ChessPosition(8, 8), rook);

        for (int i = 1; i < 9; i++) {
            addPiece(new ChessPosition(7, i), pawn);
        }
    }

    @Override
    public String toString() {
        return "Not yet implemented.";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                hash += Objects.hash(board[i][j]);
            }
        }
        return hash;
    }
}
