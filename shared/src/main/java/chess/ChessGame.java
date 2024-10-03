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
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
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

    private Collection<ChessPosition> getTeamPositions(ChessBoard chessBoard, TeamColor team) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (chessBoard.getPiece(pos) != null) {
                    if (chessBoard.getPiece(pos).getTeamColor() == team) {
                        positions.add(pos);
                    }
                }
            }
        }

        return positions;
    }

    private boolean kingInCheck(ChessBoard checker, TeamColor currColor) {
        TeamColor enemyColor = TeamColor.WHITE;
        if (currColor == TeamColor.WHITE) { enemyColor = TeamColor.BLACK; }
        Collection<ChessPosition> enemies = getTeamPositions(checker, enemyColor);

        for (var enemy : enemies) {
            ChessPiece currEnemy = new ChessPiece(checker.getPiece(enemy).getTeamColor(), checker.getPiece(enemy).getPieceType());
            Collection<ChessMove> enemyMoves = currEnemy.pieceMoves(checker, enemy);
            for (var enemyMove : enemyMoves) {
                if (checker.getPiece(enemyMove.getEndPosition()) != null) {
                    if (checker.getPiece(enemyMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                        return true;
                    }
                }
            }
        }

        return false;
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

        Collection<ChessMove> valid = new ArrayList<>();
        if (currPiece != null) {
            Collection<ChessMove> moves = currPiece.pieceMoves(board, startPosition);

            for (var move : moves) {
                ChessBoard checker = new ChessBoard(board);
                checker.addPiece(move.getEndPosition(), currPiece);
                checker.removePiece(move.getStartPosition());

                if (!kingInCheck(checker, board.getPiece(move.getStartPosition()).getTeamColor())) {
                    valid.add(move);
                }
            }
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
        if (board.getPiece(move.getStartPosition()) == null
                || board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new chess.InvalidMoveException();
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        boolean valid = false;
        for (var validMove : validMoves) {
            if (validMove.equals(move)) {
                valid = true;
                break;
            }
        }

        if (valid) {
            if (move.getPromotionPiece() != null) {
                ChessPiece promotion = new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotion);
            }
            else { board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition())); }
            board.removePiece(move.getStartPosition());

            if (teamTurn == TeamColor.WHITE) { teamTurn = TeamColor.BLACK; }
            else { teamTurn = TeamColor.WHITE; }
        }
        else { throw new chess.InvalidMoveException(); }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return kingInCheck(board, teamColor);
    }

    private boolean noValidTeamMoves(TeamColor team) {
        Collection<ChessPosition> teamPositions = getTeamPositions(board, team);
        for (var teammate : teamPositions) {
            Collection<ChessMove> moves = validMoves(teammate);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (kingInCheck(board, teamColor) && noValidTeamMoves(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return noValidTeamMoves(teamColor);
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
