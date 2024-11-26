package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMove extends UserGameCommand {
    private final ChessMove move;

    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
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
