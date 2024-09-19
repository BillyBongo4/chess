package chessRules;

import chess.*;

import java.util.Collection;

public class PawnRules extends Rules {
    public Collection<ChessMove> pieceMove(ChessPiece currPiece, ChessPosition myPosition, ChessBoard board) {
        int numForwardMoves = 1;
        if ((myPosition.getRow() == 2 && currPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
                || (myPosition.getRow() == 7 && currPiece.getTeamColor() == ChessGame.TeamColor.BLACK)) {
            numForwardMoves++;
        }

        int rowMod = 1;
        if (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) { rowMod = -1; }

        for (int i = 0; i < numForwardMoves; i++) {
            ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod, myPosition.getColumn());

            boolean validMove = ((currPosition.getRow() <= 8 && currPosition.getRow() >= 1)
                    && (currPosition.getColumn() <= 8 && currPosition.getColumn() >= 1));

            if (validMove && i == 0) {
                for (int j = 0; j < 2; j++) {
                    int modifier = 1;
                    if (j == 1) { modifier = -1; }

                    ChessPosition possibleCapture = new ChessPosition(currPosition.getRow(),
                            currPosition.getColumn() + modifier);
                    boolean validCapture = ((possibleCapture.getRow() <= 8 && possibleCapture.getRow() >= 1)
                            && (possibleCapture.getColumn() <= 8 && possibleCapture.getColumn() >= 1));
                    if (validCapture && board.getPiece(possibleCapture) != null) {
                        if (board.getPiece(possibleCapture).getTeamColor() != currPiece.getTeamColor()) {
                            if ((currPiece.getTeamColor() == ChessGame.TeamColor.WHITE && currPosition.getRow() == 8)
                                    || (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK && currPosition.getRow() == 1)) {
                                currPiece.pawnPromotion(getMoves(), myPosition, possibleCapture);
                            }
                            else {
                                ChessMove move = new ChessMove(myPosition, possibleCapture, null);
                                getMoves().add(move);
                            }
                        }
                    }
                }
            }

            if (board.getPiece(currPosition) != null) {
                break;
            }

            if (validMove) {
                if ((currPiece.getTeamColor() == ChessGame.TeamColor.WHITE && currPosition.getRow() == 8)
                        || (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK && currPosition.getRow() == 1)) {
                    currPiece.pawnPromotion(getMoves(), myPosition, currPosition);
                }
                else {
                    ChessMove move = new ChessMove(myPosition, currPosition, null);
                    getMoves().add(move);
                }

            }

            if (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) { rowMod--; }
            else { rowMod++; }
        }
        return getMoves();
    }
}
