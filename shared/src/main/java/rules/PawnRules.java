package rules;

import chess.*;

import java.util.Collection;

public class PawnRules extends Rules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            color = ChessGame.TeamColor.BLACK;
        }
        ChessPiece currPiece = new ChessPiece(color, ChessPiece.PieceType.PAWN);

        int numForward = 1;
        if ((color == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)
                || (color == ChessGame.TeamColor.WHITE) && myPosition.getRow() == 2) {
            numForward++;
        }

        int modifier = 1;
        for (int i = 0; i < numForward; i++) {
            int rowMod = modifier;
            if (color == ChessGame.TeamColor.BLACK) {
                rowMod *= -1;
            }

            ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                    myPosition.getColumn());

            if (currPosition.getRow() > 8 || currPosition.getRow() < 1) {
                break;
            }
            if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) {
                break;
            }

            int colMod = -1;
            for (int j = 0; j < 2; j++) {
                ChessPosition capturePosition = new ChessPosition(currPosition.getRow(),
                        currPosition.getColumn() + colMod);

                if (capturePosition.getRow() > 8 || capturePosition.getRow() < 1) {
                    break;
                }
                if (capturePosition.getColumn() > 8 || capturePosition.getColumn() < 1) {
                    break;
                }

                if (board.getPiece(capturePosition) != null) {
                    if (board.getPiece(capturePosition).getTeamColor()
                            == board.getPiece(myPosition).getTeamColor()) {
                        break;
                    } else {
                        if ((capturePosition.getRow() == 1 && color == ChessGame.TeamColor.BLACK)
                                || (capturePosition.getRow() == 8 && color == ChessGame.TeamColor.WHITE)) {
                            currPiece.pawnPromotion(getMoves(), myPosition, capturePosition);
                        } else {
                            ChessMove captureMove = new ChessMove(myPosition, capturePosition, null);
                            getMoves().add(captureMove);
                        }
                    }
                }
                colMod += 2;
            }

            if (board.getPiece(currPosition) != null) {
                break;
            }

            if ((currPosition.getRow() == 1 && color == ChessGame.TeamColor.BLACK)
                    || (currPosition.getRow() == 8 && color == ChessGame.TeamColor.WHITE)) {
                currPiece.pawnPromotion(getMoves(), myPosition, currPosition);
            } else {
                ChessMove move = new ChessMove(myPosition, currPosition, null);
                getMoves().add(move);
            }

            modifier++;
        }

        return getMoves();
    }
}