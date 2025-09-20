package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseMovementRule implements MovementRule {

    /**
     * Checks whether a given row and column are within bounds
     *
     * @param row row number
     * @param col column number
     * @return true if within bounds, false otherwise
     */
    private boolean withinBounds(int row, int col) {
        return row <= 8 && row > 0 && col <= 8 && col > 0;
    }

    /**
     * Calculates the possible moveset for a given piece and board
     *
     * @param board      current board
     * @param myPosition position of piece
     * @param steps      int[][] of steps the piece could take in any direction
     * @param recursive  boolean of whether the movement is recursive
     * @return Collection of ChessMoves dictating the possible moveset of the piece
     */
    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, final int[][] steps, final boolean recursive) {
        var possibleMoves = new ArrayList<ChessMove>();

        for (int[] step : steps) {
            int rowAdder = step[0];
            int colAdder = step[1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            var currentPosition = new ChessPosition(row, col);
            ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

            if (!withinBounds(row, col)) {
                continue;
            }

            if (!board.checkFriendlyPosition(currentPosition, myColor)) {
                possibleMoves.add(new ChessMove(myPosition, currentPosition, null));
            }

            boolean blocked = board.checkFriendlyPosition(currentPosition, myColor) || board.checkEnemyPosition(currentPosition, myColor);

            row += rowAdder;
            col += colAdder;

            while (recursive && !blocked && withinBounds(row, col)) {
                currentPosition = new ChessPosition(row, col);
                if (!board.checkFriendlyPosition(currentPosition, myColor)) {
                    possibleMoves.add(new ChessMove(myPosition, currentPosition, null));
                }
                blocked = board.checkFriendlyPosition(currentPosition, myColor) || board.checkEnemyPosition(currentPosition, myColor);
                row += rowAdder;
                col += colAdder;
            }
        }

        return possibleMoves;
    }

    /**
     * Generates the potential moves a piece can take, given all necessary information
     *
     * @param board      current board
     * @param myPosition current piece position
     * @param steps      int[][] of steps the piece could take in any direction
     * @param recursive  boolean of whether the movement is recursive
     * @return Collection of ChessMoves that a given piece could take
     */
    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, final int[][] steps, final boolean recursive) {
        return calculateMoves(board, myPosition, steps, recursive);
    }

    @Override
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
