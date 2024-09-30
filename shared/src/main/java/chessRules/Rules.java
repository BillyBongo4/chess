package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class Rules {
    final private Collection<ChessMove> moves = new ArrayList<>();

    protected boolean addMove(ChessBoard board, ChessPosition myPosition, int rowMod, int colMod) {
        ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                myPosition.getColumn() + colMod);

        if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { return true; }
        if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { return true; }

        boolean stop = false;
        if (board.getPiece(currPosition) != null) {
            if (board.getPiece(currPosition).getTeamColor()
                    == board.getPiece(myPosition).getTeamColor()) { return true; }
            else { stop = true; }
        }

        ChessMove move = new ChessMove(myPosition, currPosition, null);
        moves.add(move);

        return stop;
    }

    protected void diagonalMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = 0; i < 4; i++) {
            int modifier = 1;
            while (true) {
                int rowMod = modifier;
                int colMod = modifier;
                if (i == 1 || i == 2) { rowMod *= -1; }
                if (i == 2 || i == 3) { colMod *= -1; }

                if (addMove(board, myPosition, rowMod, colMod)) { break; }

                modifier++;
            }
        }
    }

    protected void straightMoves(ChessBoard board, ChessPosition myPosition) {
        for (int i = 0; i < 4; i++) {
            int modifier = 1;
            while (true) {
                int rowMod = modifier;
                int colMod = modifier;
                if (i == 1 || i == 3) { rowMod = 0; }
                if (i == 0 || i == 2) { colMod = 0; }
                if (i == 2) { rowMod *= -1; }
                if (i == 3) { colMod *= -1; }

                if (addMove(board, myPosition, rowMod, colMod)) { break; }

                modifier++;
            }
        }
    }

    protected Collection<ChessMove> getMoves() { return moves; }
}
