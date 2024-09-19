package chess;

import chessRules.BishopRules;
import chessRules.QueenRules;
import chessRules.RookRules;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    PieceType type;
    boolean movedBefore = false;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public void pawnPromotion(Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition currPosition) {
        for (int j = 0; j < 4; j++) {
            PieceType promotion;
            if (j == 0) { promotion = PieceType.QUEEN; }
            else if (j == 1) { promotion = PieceType.BISHOP; }
            else if (j == 2) { promotion = PieceType.ROOK; }
            else { promotion = PieceType.KNIGHT; }

            ChessMove move = new ChessMove(myPosition, currPosition, promotion);
            moves.add(move);
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*Collection<ChessMove> moves = new ArrayList<>();
        if (type == PieceType.BISHOP) {
            for (int i = 0; i < 4; i++) {
                int modifier = 1;
                while (true) {
                    int rowMod = modifier;
                    int colMod = modifier;
                    if (i == 1 || i == 3) { rowMod *= -1; }
                    if (i == 2 || i == 3) { colMod *= -1; }

                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                            myPosition.getColumn() + colMod);

                    if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                    if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { break; }
                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() == getTeamColor()) {
                            break;
                        }
                    }

                    ChessMove move = new ChessMove(myPosition, currPosition, null);
                    moves.add(move);
                    modifier++;

                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() != getTeamColor()) {
                            break;
                        }
                    }
                }
            }
        }
        else if (type == PieceType.KING) {
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
                            if (board.getPiece(currPosition).getTeamColor() == getTeamColor()) {
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
        }
        else if (type == PieceType.KNIGHT) {
            for (int i = 0; i < 4; i++) {
                int rowMod = 2;
                int colMod = 1;

                if (i == 1 || i == 3) {
                    rowMod--;
                    colMod++;

                    if (i == 3) { colMod *= -1; }
                }
                else if (i == 2) { rowMod *= -1; }

                for (int j = 0; j < 2; j++) {
                    if ((i == 0 || i == 2) && j == 1) { colMod *= -1; }
                    else if ((i == 1 || i == 3) && j == 1) { rowMod *= -1; }

                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                            myPosition.getColumn() + colMod);

                    boolean validSpace = (currPosition.getRow() <= 8 && currPosition.getRow() >= 1)
                            && (currPosition.getColumn() <= 8 && currPosition.getColumn() >= 1);


                    if (validSpace && board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() == getTeamColor()) {
                            validSpace = false;
                        }
                    }

                    if (validSpace) {
                        ChessMove move = new ChessMove(myPosition, currPosition, null);
                        moves.add(move);
                    }
                }
            }
        }
        else if (type == PieceType.PAWN) {
            int numForwardMoves = 1;
            if (!movedBefore && ((myPosition.getRow() == 2 && getTeamColor() == ChessGame.TeamColor.WHITE)
                    || (myPosition.getRow() == 7) && getTeamColor() == ChessGame.TeamColor.BLACK)) {
                numForwardMoves++;
            }

            int rowMod = 1;
            if (getTeamColor() == ChessGame.TeamColor.BLACK) { rowMod = -1; }

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
                            if (board.getPiece(possibleCapture).getTeamColor() != getTeamColor()) {
                                if ((getTeamColor() == ChessGame.TeamColor.WHITE && currPosition.getRow() == 8)
                                        || (getTeamColor() == ChessGame.TeamColor.BLACK && currPosition.getRow() == 1)) {
                                    pawnPromotion(moves, myPosition, possibleCapture);
                                }
                                else {
                                    ChessMove move = new ChessMove(myPosition, possibleCapture, null);
                                    moves.add(move);
                                }
                            }
                        }
                    }
                }

                if (board.getPiece(currPosition) != null) {
                    break;
                }

                if (validMove) {
                    if ((getTeamColor() == ChessGame.TeamColor.WHITE && currPosition.getRow() == 8)
                            || (getTeamColor() == ChessGame.TeamColor.BLACK && currPosition.getRow() == 1)) {
                        pawnPromotion(moves, myPosition, currPosition);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, currPosition, null);
                        moves.add(move);
                    }

                }

                if (getTeamColor() == ChessGame.TeamColor.BLACK) { rowMod--; }
                else { rowMod++; }
            }
        }
        else if (type == PieceType.QUEEN) {
            for (int i = 0; i < 4; i++) {
                int modifier = 1;
                while (true) {
                    int rowMod = modifier;
                    int colMod = modifier;
                    if (i == 1 || i == 3) { rowMod *= -1; }
                    if (i == 2 || i == 3) { colMod *= -1; }

                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                            myPosition.getColumn() + colMod);

                    if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                    if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { break; }
                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() == getTeamColor()) {
                            break;
                        }
                    }

                    ChessMove move = new ChessMove(myPosition, currPosition, null);
                    moves.add(move);
                    modifier++;

                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() != getTeamColor()) {
                            break;
                        }
                    }
                }
            }

            for (int i = 0; i < 4; i++) {
                int modifier = 1;
                while (true) {
                    int rowMod = modifier;
                    int colMod = modifier;
                    if (i == 1 || i == 3) { rowMod = 0; }
                    else if (i == 2) { rowMod *= -1; }
                    if (i == 0 || i == 2) { colMod = 0; }
                    else if (i == 3) { colMod *= -1; }

                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                            myPosition.getColumn() + colMod);

                    if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                    if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { break; }
                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() == getTeamColor()) {
                            break;
                        }
                    }

                    ChessMove move = new ChessMove(myPosition, currPosition, null);
                    moves.add(move);
                    modifier++;

                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() != getTeamColor()) {
                            break;
                        }
                    }
                }
            }
        }
        else if (type == PieceType.ROOK) {
            for (int i = 0; i < 4; i++) {
                int modifier = 1;
                while (true) {
                    int rowMod = modifier;
                    int colMod = modifier;
                    if (i == 1 || i == 3) { rowMod = 0; }
                    else if (i == 2) { rowMod *= -1; }
                    if (i == 0 || i == 2) { colMod = 0; }
                    else if (i == 3) { colMod *= -1; }

                    ChessPosition currPosition = new ChessPosition(myPosition.getRow() + rowMod,
                            myPosition.getColumn() + colMod);

                    if (currPosition.getRow() > 8 || currPosition.getRow() < 1) { break; }
                    if (currPosition.getColumn() > 8 || currPosition.getColumn() < 1) { break; }
                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() == getTeamColor()) {
                            break;
                        }
                    }

                    ChessMove move = new ChessMove(myPosition, currPosition, null);
                    moves.add(move);
                    modifier++;

                    if (board.getPiece(currPosition) != null) {
                        if (board.getPiece(currPosition).getTeamColor() != getTeamColor()) {
                            break;
                        }
                    }
                }
            }
        }
        return moves;*/
        if (type == PieceType.BISHOP) { return new BishopRules().pieceMoves(this, myPosition, board); }
        else if (type == PieceType.QUEEN) { return new QueenRules().pieceMove(this, myPosition, board); }
        else if (type == PieceType.ROOK) { return new RookRules().pieceMove(this, myPosition, board); }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) { return true; }
        if (object == null || getClass() != object.getClass()) { return false; }
        ChessPiece piece = (ChessPiece) object;
        return (pieceColor.equals(piece.getTeamColor()) && type.equals(piece.getPieceType()));
    }

    @Override
    public int hashCode() {
        var typeCode = (type == null ? 9 : type.ordinal());
        var colorCode = (type == null ? 7 : pieceColor.ordinal());
        return (71 * colorCode) + typeCode;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", pieceColor.toString(), type.toString());
    }
    // test
}
