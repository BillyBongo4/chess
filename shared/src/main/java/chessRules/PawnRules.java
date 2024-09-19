package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class PawnRules extends Rules {
    public Collection<ChessMove> pieceMove(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        return getMoves();
    }
}
