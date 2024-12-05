package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

public class MakeMove extends UserGameCommand {
    private final ChessMove move;
    private final ChessGame game;
    private final String color;

    public MakeMove(String authToken, Integer gameID, ChessMove move, ChessGame game, String color) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.game = game;
        this.color = color;
    }

    public ChessMove getMove() {
        return move;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        MakeMove makeMove = (MakeMove) o;
        return Objects.equals(getMove(), makeMove.getMove());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMove());
    }
}
