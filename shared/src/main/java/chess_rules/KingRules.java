package chess_rules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KingRules extends Rules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int rowMod = -1;
        for (int i = 0; i < 3; i++) {
            int colMod = -1;
            for (int j = 0; j < 3; j++) {
                addMove(board, myPosition, rowMod, colMod);

                colMod++;
            }
            rowMod++;
        }

        return getMoves();
    }
}
