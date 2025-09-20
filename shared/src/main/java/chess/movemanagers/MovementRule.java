package chess.movemanagers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface MovementRule {
    /**
     * Returns a collection of ChessMoves given a particular board and piece
     *
     * @param board      current ChessBoard
     * @param myPosition current position of piece
     * @return Collection of ChessMoves defining the possible moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
