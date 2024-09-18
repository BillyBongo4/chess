package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) { return true; }
        if (object == null || getClass() != object.getClass()) { return false; }
        ChessMove move = (ChessMove) object;
        return (startPosition.equals(move.getStartPosition()) && endPosition.equals(move.getEndPosition())
                && ((promotionPiece == null && move.getPromotionPiece() == null) || getPromotionPiece().equals(move.getPromotionPiece())));
    }

    @Override
    public int hashCode() {
        var promotionCode = (promotionPiece == null ? 9 : promotionPiece.ordinal());
        return (71 * startPosition.hashCode()) + endPosition.hashCode() + promotionCode;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", startPosition.toString(), endPosition.toString());
    }
    //test to see if commit is working
}
