package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor teamTurn;
    boolean whiteCheck;
    boolean blackCheck;
    boolean whiteCheckmate;
    boolean blackCheckmate;
    boolean whiteStalemate;
    boolean blackStalemate;
    public ChessGame() {
        board = null;
        //teamTurn = TeamColor.WHITE;
        whiteCheck = false;
        blackCheck = false;
        whiteCheckmate = false;
        blackCheckmate = false;
        whiteStalemate = false;
        blackStalemate = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null) { return null; }

        ChessPiece currPiece = board.getPiece(startPosition);

        Collection<ChessMove> moves = currPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();

        for (var move : moves) {
            ChessBoard checker = new ChessBoard(board);
            checker.addPiece(move.getEndPosition(), currPiece);
            checker.removePiece(move.getStartPosition());

            Collection<ChessPosition> enemies = new ArrayList<>();
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition pos = new ChessPosition(i, j);
                    if (board.getPiece(pos) != null) {
                        if (board.getPiece(pos).getTeamColor() != currPiece.getTeamColor()) {
                            enemies.add(pos);
                        }
                    }
                }
            }

            boolean validMove = true;
            for (var enemy : enemies) {
                ChessPiece currEnemy = new ChessPiece(checker.getPiece(enemy).getTeamColor(), checker.getPiece(enemy).getPieceType());
                Collection<ChessMove> enemyMoves = currEnemy.pieceMoves(checker, enemy);
                for (var enemyMove : enemyMoves) {
                    if (checker.getPiece(enemyMove.getEndPosition()) != null) {
                        if (checker.getPiece(enemyMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                            validMove = false;
                            break;
                        }
                    }
                }
            }

            if (validMove) { valid.add(move); }
        }

        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        for (var validMove : validMoves) {
            if (validMove == move) {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.removePiece(move.getStartPosition());
            }
            else { throw new InvalidMoveException(); }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return teamColor == TeamColor.WHITE ? whiteCheck : blackCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return teamColor == TeamColor.WHITE ? whiteCheckmate : blackCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return teamColor == TeamColor.WHITE ? whiteStalemate : blackStalemate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
