package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class Rules {
    Collection<ChessMove> moves = new ArrayList<>();

    protected void diagonalMoves(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        for (int i = 0; i < 4; i++) {
            int modifier = 1;
            while (true) {
                int rowMod = modifier;
                int colMod = modifier;
                if (i == 1 || i == 3) { rowMod *= -1; }
                if (i == 2 || i == 3) { colMod *= -1; }

                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                        myPosition.getColumn() + colMod);

                if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { break; }
                if (board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() == currPiece.getTeamColor()) {
                        break;
                    }
                }

                ChessMove move = new ChessMove(myPosition, currPosition, null);
                moves.add(move);
                modifier++;

                if (board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() != currPiece.getTeamColor()) {
                        break;
                    }
                }
            }
        }
    }

    protected void straightMoves(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        for (int i = 0; i < 4; i++) {
            int modifier = 1;
            while (true) {
                int rowMod = modifier;
                int colMod = modifier;
                if (i == 1 || i == 3) { rowMod = 0; }
                else if (i == 2) { rowMod *= -1; }
                if (i == 0 || i == 2) { colMod = 0; }
                else if (i == 3) { colMod *= -1; }

                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                        myPosition.getColumn() + colMod);

                if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { break; }
                if (board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() == currPiece.getTeamColor()) {
                        break;
                    }
                }

                ChessMove move = new ChessMove(myPosition, currPosition, null);
                moves.add(move);
                modifier++;

                if (board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() != currPiece.getTeamColor()) {
                        break;
                    }
                }
            }
        }
    }

    public Collection<ChessMove> getMoves() {
        return moves;
    }
}
