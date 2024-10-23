package rules;

import chess.*;

import java.util.Collection;

public class PawnRules extends Rules {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = getPieceColor(board, myPosition);
        ChessPiece currPiece = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        int numForward = calculateNumForward(color, myPosition);

        int modifier = 1;
        for (int i = 0; i < numForward; i++) {
            int rowMod = color == ChessGame.TeamColor.BLACK ? -modifier : modifier;
            ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod, myPosition.getColumn());

            if (isPositionOutOfBounds(currPosition)) {
                break;
            }

            handleCaptures(board, currPiece, myPosition, currPosition);

            if (board.getPiece(currPosition) != null) {
                break;
            }

            addMoveOrPromotion(currPiece, myPosition, currPosition, color);
            modifier++;
        }
        return getMoves();
    }

    private ChessGame.TeamColor getPieceColor(ChessBoard board, ChessPosition myPosition) {
        return board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
    }

    private int calculateNumForward(ChessGame.TeamColor color, ChessPosition myPosition) {
        return (color == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) ||
                (color == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ? 2 : 1;
    }

    private boolean isPositionOutOfBounds(ChessPosition position) {
        return position.getRow() > 8 || position.getRow() < 1 ||
                position.getColumn() > 8 || position.getColumn() < 1;
    }

    private void handleCaptures(ChessBoard board, ChessPiece currPiece, ChessPosition myPosition, ChessPosition currPosition) {
        int colMod = -1;
        for (int j = 0; j < 2; j++) {
            ChessPosition capturePosition = new ChessPosition(currPosition.getRow(), currPosition.getColumn() + colMod);

            if (isPositionOutOfBounds(capturePosition)) {
                break;
            }

            ChessPiece capturedPiece = board.getPiece(capturePosition);
            if (capturedPiece != null) {
                if (capturedPiece.getTeamColor() == currPiece.getTeamColor()) {
                    break;
                } else {
                    addCaptureMoveOrPromotion(currPiece, myPosition, capturePosition, currPiece.getTeamColor());
                }
            }
            colMod += 2;
        }
    }

    private void addCaptureMoveOrPromotion(ChessPiece currPiece, ChessPosition from, ChessPosition to, ChessGame.TeamColor color) {
        if ((to.getRow() == 1 && color == ChessGame.TeamColor.BLACK) || (to.getRow() == 8 && color == ChessGame.TeamColor.WHITE)) {
            currPiece.pawnPromotion(getMoves(), from, to);
        } else {
            ChessMove captureMove = new ChessMove(from, to, null);
            getMoves().add(captureMove);
        }
    }

    private void addMoveOrPromotion(ChessPiece currPiece, ChessPosition from, ChessPosition to, ChessGame.TeamColor color) {
        if ((to.getRow() == 1 && color == ChessGame.TeamColor.BLACK) || (to.getRow() == 8 && color == ChessGame.TeamColor.WHITE)) {
            currPiece.pawnPromotion(getMoves(), from, to);
        } else {
            ChessMove move = new ChessMove(from, to, null);
            getMoves().add(move);
        }
    }
}
