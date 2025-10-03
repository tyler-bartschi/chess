package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final ChessBoard chessboard;
    private TeamColor teamTurn;
    private final ValidMoveFilter moveFilter;

    public ChessGame() {
        chessboard = new ChessBoard();
        chessboard.resetBoard();
        teamTurn = TeamColor.WHITE;
        moveFilter = new ValidMoveFilter();
    }

    /**
     * Finds the king of the given color on a given board
     *
     * @param board   current board
     * @param myColor color of the king to find
     * @return the ChessPosition containing the king, null if it cannot be found
     */
    private ChessPosition findKing(ChessBoard board, TeamColor myColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece == null) {
                    continue;
                }
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == myColor) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if there is a possible move to protect the king
     *
     * @param myColor King's color
     * @return true if there is a move to protect the king, false otherwise
     */
    private boolean checkKingProtection(TeamColor myColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = chessboard.getPiece(currentPosition);
                if (currentPiece == null || currentPiece.getTeamColor() != myColor
                        || currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    continue;
                }

                Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(chessboard, currentPosition);

                for (ChessMove move : possibleMoves) {
                    ChessBoard tempBoard = makeMoveForceful(move);
                    ChessPosition kingPosition = findKing(tempBoard, myColor);
                    if (!moveFilter.checkIfInCheck(tempBoard, kingPosition, myColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Make a new ChessBoard with a new move made, no checks are made for if the move is valid.
     *
     * @param move ChessMove to make
     * @return ChessBoard with the given move made
     */
    private ChessBoard makeMoveForceful(ChessMove move) {
        ChessBoard newBoard = new ChessBoard();
        newBoard.setGivenBoard(chessboard);
        newBoard.makeMove(move);
        return newBoard;
    }

    private ChessMove expandCastleMove(ChessMove castleMove) {
        int row = castleMove.getStartPosition().getRow();
        if (castleMove.getEndPosition().getColumn() == 7) {
            return new ChessMove(castleMove.getStartPosition(), new ChessPosition(row, 6), null);
        } else {
            return new ChessMove(castleMove.getStartPosition(), new ChessPosition(row, 4), null);
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = chessboard.getPiece(startPosition);
        if (myPiece == null) {
            return null;
        }
        TeamColor myColor = myPiece.getTeamColor();
        boolean kingFlag = myPiece.getPieceType() == ChessPiece.PieceType.KING;

        Collection<ChessMove> possibleMoves = myPiece.pieceMoves(chessboard, startPosition);
        ArrayList<ChessMove> valid = new ArrayList<ChessMove>();

        for (ChessMove currentMove : possibleMoves) {
            if (kingFlag && chessboard.kingWantsCastle(currentMove)) {
                ChessMove expanded = expandCastleMove(currentMove);
                ChessBoard tempBoard = makeMoveForceful(expanded);
                ChessPosition kingPosition = findKing(tempBoard, myColor);

                ChessBoard tempBoard2 = makeMoveForceful(currentMove);
                ChessPosition kingPosition2 = findKing(tempBoard2, myColor);
                if (!moveFilter.checkIfInCheck(tempBoard, kingPosition, myColor) && !moveFilter.checkIfInCheck(tempBoard2, kingPosition2, myColor) && !moveFilter.checkIfInCheck(chessboard, findKing(chessboard, myColor), myColor)) {
                    valid.add(currentMove);
                }
            } else {
                ChessBoard tempBoard = makeMoveForceful(currentMove);
                ChessPosition kingPosition = findKing(tempBoard, myColor);
                if (!moveFilter.checkIfInCheck(tempBoard, kingPosition, myColor)) {
                    valid.add(currentMove);
                }
            }
        }
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (chessboard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece to move at " + move.getStartPosition());
        }

        if (chessboard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Move out of turn. Current turn is: " + teamTurn);
        }

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (valid == null) {
            throw new InvalidMoveException("No moves are valid for given piece.");
        }

        if (valid.contains(move)) {
            chessboard.makeMove(move);
            setTeamTurn(teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        } else {
            throw new InvalidMoveException("Invalid move provided: " + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(chessboard, teamColor);
        if (kingPosition == null) {
            throw new RuntimeException("Could not find King.");
        }

        return moveFilter.checkIfInCheck(chessboard, kingPosition, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(chessboard, teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingPosition);
        return isInCheck(teamColor) && kingMoves.isEmpty() && !checkKingProtection(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // find all the valid moves
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = chessboard.getPiece(currentPosition);
                if (currentPiece == null || currentPiece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> valid = validMoves(currentPosition);
                if (!valid.isEmpty()) {
                    return false;
                }
            }
        }

        return !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessboard.setGivenBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessboard;
    }

    @Override
    public String toString() {
        return String.format("ChessGame{Turn: %s, board: %s}", teamTurn.toString(), chessboard.gameToString());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessGame that) {
            return chessboard.equals(that.chessboard) && teamTurn == that.teamTurn;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessboard.hashCode(), teamTurn);
    }
}
