package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] chessBoard;
    public ChessBoard() {
        chessBoard = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //throw new RuntimeException("Not implemented");
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[i].length; j++) {
                if (i == 0) {
                    if (j == 0 || j == 7) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK); }
                    else if (j == 1 || j == 6) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT); }
                    else if (j == 2 || j == 5) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP); }
                    else if (j == 3) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN); }
                    else if (j == 4) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING); }
                }
                else if (i == 1) { chessBoard[i][j] =
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN); }
                else if (i == 6) { chessBoard[i][j] =
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN); }
                else if (i == 7) {
                    if (j == 0 || j == 7) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK); }
                    else if (j == 1 || j == 6) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT); }
                    else if (j == 2 || j == 5) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP); }
                    else if (j == 3) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN); }
                    else if (j == 4) { chessBoard[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING); }
                }
                else { chessBoard[i][j] = null; }
            }

        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) { return true; }
        if (object == null || getClass() != object.getClass()) { return false; }
        ChessBoard board = (ChessBoard) object;
        return (Arrays.deepEquals(chessBoard, board.chessBoard));
    }

    @Override
    public int hashCode() {
        //var promotionCode = (promotionPiece == null ? 9 : promotionPiece.ordinal());
        return (71 * Arrays.deepHashCode(chessBoard));
    }

    @Override
    public String toString() {
        return String.format("%s", Arrays.deepToString(chessBoard));
    }
    //test
}
