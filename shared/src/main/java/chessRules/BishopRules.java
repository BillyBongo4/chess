package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class BishopRules extends Rules {
    public Collection<ChessMove> moves(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        super.diagonalMoves(currPiece, myPosition, board);
        return getMoves();
    }
}
