package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class KnightRules extends Rules {
    public Collection<ChessMove> pieceMove(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        for (int i = 0; i < 4; i++) {
            int rowMod = 2;
            int colMod = 1;

            if (i == 1 || i == 3) {
                rowMod--;
                colMod++;

                if (i == 3) { colMod *= -1; }
            }
            else if (i == 2) { rowMod *= -1; }

            for (int j = 0; j < 2; j++) {
                if ((i == 0 || i == 2) && j == 1) { colMod *= -1; }
                else if ((i == 1 || i == 3) && j == 1) { rowMod *= -1; }

                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                        myPosition.getColumn() + colMod);

                boolean validSpace = (currPosition.getRow() <= 8 && currPosition.getRow() >= 1)
                        && (currPosition.getColumn() <= 8 && currPosition.getColumn() >= 1);


                if (validSpace && board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() == currPiece.getTeamColor()) {
                        validSpace = false;
                    }
                }

                if (validSpace) {
                    ChessMove move = new ChessMove(myPosition, currPosition, null);
                    getMoves().add(move);
                }
            }
        }
        return getMoves();
    }
}
