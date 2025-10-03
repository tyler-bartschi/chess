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

    private boolean enPassantPossible;
    private ChessPosition doubleMovedPawn;

    public ChessGame() {
        chessboard = new ChessBoard();
        chessboard.resetBoard();
        teamTurn = TeamColor.WHITE;
        moveFilter = new ValidMoveFilter();
        enPassantPossible = false;
        doubleMovedPawn = null;
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

        ChessPosition kingPosition = findKing(chessboard, myColor);

        for (ChessMove currentMove : possibleMoves) {
            if (kingFlag) {
                if (chessboard.getMoveManager().kingWantsCastle(currentMove)) {
                    addCastle(currentMove, myColor, valid);
                } else {
                    kingPosition = currentMove.getEndPosition();
                    addValidMove(currentMove, kingPosition, myColor, valid);
                }
            } else {
                addValidMove(currentMove, kingPosition, myColor, valid);
            }
        }

        addEnPassant(myPiece, startPosition, valid);
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
            checkPawnMoveAndUpdate(move);
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

    /**
     * Checks if a pawn is moving, and if it is a double move that could result in an en passant.
     * Updates trackers accordingly.
     *
     * @param move move to be made
     */
    private void checkPawnMoveAndUpdate(ChessMove move) {
        ChessPiece myPiece = chessboard.getPiece(move.getStartPosition());
        int startRow = move.getStartPosition().getRow();
        int endRow = move.getEndPosition().getRow();

        if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN && startRow + 2 == endRow || startRow - 2 == endRow) {
            enPassantPossible = true;
            doubleMovedPawn = move.getEndPosition();
        } else {
            enPassantPossible = false;
            doubleMovedPawn = null;
        }
    }

    /**
     * Checks if a move is valid, and if it is, adds it to the Collection
     *
     * @param currentMove  move to be made
     * @param kingPosition king's current position
     * @param myColor      team color
     * @param valid        Collection of valid ChessMoves
     */
    private void addValidMove(ChessMove currentMove, ChessPosition kingPosition, TeamColor myColor, Collection<ChessMove> valid) {
        ChessBoard tempBoard = makeMoveForceful(currentMove);
        if (!moveFilter.checkIfInCheck(tempBoard, kingPosition, myColor)) {
            valid.add(currentMove);
        }
    }

    /**
     * Checks if a castle is possible, and if it is, adds the moves to the Collection
     *
     * @param currentMove castle move
     * @param myColor     team color
     * @param valid       Collection of valid ChessMoves
     */
    private void addCastle(ChessMove currentMove, TeamColor myColor, Collection<ChessMove> valid) {
        if (moveFilter.checkIfInCheck(chessboard, currentMove.getStartPosition(), myColor)) {
            return;
        }

        ChessMove expanded = expandCastleMove(currentMove);
        ChessBoard tempBoard = makeMoveForceful(expanded);
        ChessPosition kingPosition = expanded.getEndPosition();

        ChessBoard tempBoard2 = makeMoveForceful(currentMove);
        ChessPosition kingPosition2 = currentMove.getEndPosition();
        if (!moveFilter.checkIfInCheck(tempBoard, kingPosition, myColor) && !moveFilter.checkIfInCheck(tempBoard2, kingPosition2, myColor)) {
            valid.add(currentMove);
        }
    }

    /**
     * Checks the pawns position, to see if an enPassant is possible
     *
     * @param pawnPosition pawns position
     * @return true if an enPassant is possible, false if not
     */
    private boolean checkPawnPosition(ChessPosition pawnPosition) {
        int pawnRow = pawnPosition.getRow();
        int pawnCol = pawnPosition.getColumn();
        int doubleMovedPawnRow = doubleMovedPawn.getRow();
        int doubleMovedPawnCol = doubleMovedPawn.getColumn();
        return (pawnCol == doubleMovedPawnCol - 1 || pawnCol == doubleMovedPawnCol + 1) && pawnRow == doubleMovedPawnRow;
    }

    /**
     * Adds the enPassant move if it is valid
     *
     * @param myPiece       the current ChessPiece
     * @param startPosition the ChessPiece's start position
     * @param valid         collection of valid ChessMoves
     */
    private void addEnPassant(ChessPiece myPiece, ChessPosition startPosition, Collection<ChessMove> valid) {
        boolean pawnFlag = myPiece.getPieceType() == ChessPiece.PieceType.PAWN;
        if (pawnFlag && enPassantPossible) {
            if (checkPawnPosition(startPosition)) {
                int newRow = doubleMovedPawn.getRow() == 4 ? 3 : 6;
                valid.add(new ChessMove(startPosition, new ChessPosition(newRow, doubleMovedPawn.getColumn()), null));
            }
        }
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

    /**
     * Returns the additional move that must be checked for a castle to be safe
     *
     * @param castleMove ChessMove that represents a castle
     * @return the ChessMove that represents the square the king crosses
     */
    private ChessMove expandCastleMove(ChessMove castleMove) {
        int row = castleMove.getStartPosition().getRow();
        if (castleMove.getEndPosition().getColumn() == 7) {
            return new ChessMove(castleMove.getStartPosition(), new ChessPosition(row, 6), null);
        } else {
            return new ChessMove(castleMove.getStartPosition(), new ChessPosition(row, 4), null);
        }
    }
}
