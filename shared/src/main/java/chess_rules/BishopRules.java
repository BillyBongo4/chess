package chess_rules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class BishopRules extends Rules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        diagonalMoves(board, myPosition);
        return getMoves();
    }
}
