package chess;

import chessRules.*;

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
        if (type == PieceType.BISHOP) { return new BishopRules().pieceMoves(this, myPosition, board); }
        else if (type == PieceType.KING) { return new KingRules().pieceMove(this, myPosition, board); }
        else if (type == PieceType.KNIGHT) { return new KnightRules().pieceMove(this, myPosition, board); }
        else if (type == PieceType.PAWN) { return new PawnRules().pieceMove(this, myPosition, board); }
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
