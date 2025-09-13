package chess.movecalculators;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public class PawnMoveCalculator {
    private static int[][] generateSteps(ChessPosition myPosition, ChessGame.TeamColor myColor) {
        int[][] steps = new int[4][2];

        if (myColor == ChessGame.TeamColor.WHITE) {
            steps[0] = new int[]{1, 0};
            if (myPosition.getRow() == 2) {
                steps[1] = new int[]{2, 0};
            }
            steps[2] = new int[]{1, -1};
            steps[3] = new int[]{1, 1};
        } else {
            steps[0] = new int[]{-1, 0};
            if (myPosition.getRow() == 7) {
                steps[1] = new int[]{-2, 0};
            }
            steps[2] = new int[]{-1, -1};
            steps[3] = new int[]{-1, 1};
        }

        return steps;
    }

    private static boolean checkPawnPositions(ChessBoard board, ChessPosition currentPosition, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        ChessPosition lowerPosition;
        if (myColor == ChessGame.TeamColor.WHITE) {
            lowerPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
        } else {
            lowerPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());

        }
        return ChessBoard.checkFriendlyPresence(board, currentPosition, myPosition) || ChessBoard.checkFriendlyPresence(board, lowerPosition, myPosition)
                || ChessBoard.checkEnemyPresence(board, currentPosition, myPosition) || ChessBoard.checkEnemyPresence(board, lowerPosition, myPosition);
    }

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        final int[][] steps = generateSteps(myPosition, myColor);
        final int[] invalidStep = {0, 0};

        for (int i = 0; i < steps.length; i++) {
            int[] step = steps[i];
            int rowAdder = step[0];
            int colAdder = step[1];
            int row = myPosition.getRow() + rowAdder;
            int col = myPosition.getColumn() + colAdder;

            if (row > 8 || row < 1 || col > 8 || col < 1) {
                continue;
            }

            if (i == 0) {
                if (ChessBoard.checkFriendlyPresence(board, new ChessPosition(row, col), myPosition) || ChessBoard.checkEnemyPresence(board, new ChessPosition(row, col), myPosition)) {
                    continue;
                } else {
                    if (row == 8 || row == 1) {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.ROOK));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.BISHOP));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.KNIGHT));
                    } else {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                }
            } else if (i == 1) {
                if (Arrays.equals(step, invalidStep) || checkPawnPositions(board, new ChessPosition(row, col), myPosition, myColor)) {
                    continue;
                } else {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            } else {
                if (ChessBoard.checkEnemyPresence(board, new ChessPosition(row, col), myPosition)) {
                    if (row == 8 || row == 1) {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.ROOK));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.BISHOP));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), ChessPiece.PieceType.KNIGHT));
                    } else {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                }
            }
        }
        return possibleMoves;
    }
}