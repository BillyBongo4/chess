package chess_rules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KnightRules extends Rules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int modifier = 2;
        for (int i = 0; i < 4; i++) {
            int rowMod = modifier;
            int colMod = modifier;
            if (i == 1 || i == 3) {
                rowMod = -1;
            }
            if (i == 0 || i == 2) {
                colMod = -1;
            }
            if (i == 2) {
                rowMod *= -1;
            }
            if (i == 3) {
                colMod *= -1;
            }

            for (int j = 0; j < 2; j++) {
                addMove(board, myPosition, rowMod, colMod);

                if (i == 1 || i == 3) {
                    rowMod += 2;
                }
                if (i == 0 || i == 2) {
                    colMod += 2;
                }
            }
        }

        return getMoves();
    }
}
