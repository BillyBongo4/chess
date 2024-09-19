package chessRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class QueenRules extends Rules {
    public Collection<ChessMove> pieceMove(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        super.diagonalMoves(currPiece, myPosition, board);
        super.straightMoves(currPiece, myPosition, board);
        return getMoves();
    }
}
