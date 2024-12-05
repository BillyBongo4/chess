package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

public class Leave extends UserGameCommand {
    private final String color;

    public Leave(String authToken, Integer gameID, String color) {
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        this.color = color;
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

        Leave leave = (Leave) o;
        return Objects.equals(getColor(), leave.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getColor());
    }
}
