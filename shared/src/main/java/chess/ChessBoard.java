package chess;

import chess.movemanagers.MoveManager;
import chess.movemanagers.MovementRule;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];
    private final MoveManager moveManager;

    public ChessBoard() {
        moveManager = new MoveManager();
    }

    /**
     * Adds all pieces of a single color to the board
     *
     * @param color Team Color
     */
    private void addAllPieces(ChessGame.TeamColor color) {
        ChessPiece[] pieces = {
                new ChessPiece(color, ChessPiece.PieceType.ROOK),
                new ChessPiece(color, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(color, ChessPiece.PieceType.BISHOP),
                new ChessPiece(color, ChessPiece.PieceType.QUEEN),
                new ChessPiece(color, ChessPiece.PieceType.KING),
                new ChessPiece(color, ChessPiece.PieceType.BISHOP),
                new ChessPiece(color, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(color, ChessPiece.PieceType.ROOK)
        };

        int row = color == ChessGame.TeamColor.WHITE ? 1 : 8;
        int pawnRow = color == ChessGame.TeamColor.WHITE ? 2 : 7;

        for (int i = 0; i < pieces.length; i++) {
            addPiece(new ChessPosition(row, i + 1), pieces[i]);
            addPiece(new ChessPosition(pawnRow, i + 1), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    public MovementRule getMoveManager() {
        return moveManager;
    }

    /**
     * Checks if a position has a friendly piece on it
     *
     * @param position position to check
     * @param color    color to verify against
     * @return true if friendly present at position, false otherwise
     */
    public boolean checkFriendlyPosition(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece potential = getPiece(position);
        return potential != null && potential.getTeamColor() == color;
    }

    /**
     * Checks if a position has an enemy piece on it
     *
     * @param position position to check
     * @param color    color to verify against
     * @return true if enemy present at position, false otherwise
     */
    public boolean checkEnemyPosition(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece potential = getPiece(position);
        return potential != null && potential.getTeamColor() != color;
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

    public void makeMove(ChessMove move) {
        ChessPiece currentPiece = getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            currentPiece = new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece());
        }
        addPiece(move.getEndPosition(), currentPiece);
        addPiece(move.getStartPosition(), null);
        currentPiece.setHasMoved();
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        addAllPieces(ChessGame.TeamColor.WHITE);
        addAllPieces(ChessGame.TeamColor.BLACK);
    }

    /**
     * Sets a chessboard to match a given board
     *
     * @param givenBoard board to match
     */
    public void setGivenBoard(ChessBoard givenBoard) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = givenBoard.getPiece(currentPosition);
                if (currentPiece == null) {
                    addPiece(currentPosition, null);
                } else {
                    ChessPiece newPiece = new ChessPiece(currentPiece.getTeamColor(), currentPiece.getPieceType());
                    addPiece(currentPosition, newPiece);
                }
            }
        }
    }

    /**
     * Returns a string for use with the ChessGame object
     *
     * @return string representation of the board
     */
    public String gameToString() {
        return Arrays.deepToString(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessBoard that) {
            return Arrays.deepEquals(board, that.board);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                code += Objects.hash(board[i][j]);
            }
        }
        return code;
    }
}
