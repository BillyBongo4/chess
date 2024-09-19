package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class KingRules extends Rules {
    public Collection<ChessMove> pieceMove(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        int rowMod = 1;
        for (int i = 0; i < 3; i++) {
            int colMod = 1;
            for (int j = 0; j < 3; j++) {
                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                        myPosition.getColumn() + colMod);

                boolean validSpace = true;
                if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                if (currPosition.getColumn() <= 8 && currPosition.getColumn() >= 1
                        && currPosition != myPosition) {
                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() == currPiece.getTeamColor()) {
                            validSpace = false;
                        }
                    }
                    if (validSpace) {
                        ChessMove move = new ChessMove(myPosition, currPosition, null);
                        moves.add(move);
                    }
                }

                colMod--;
            }
            rowMod--;
        }

        return getMoves();
    }
}
