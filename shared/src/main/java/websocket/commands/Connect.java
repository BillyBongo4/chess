package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

public class Connect extends UserGameCommand {
    private final String username;
    private final String color;

    public Connect(String authToken, Integer gameID, String username, String color) {
        super(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.username = username;
        this.color = color;
    }

    public String getUsername() {
        return username;
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

        Connect connect = (Connect) o;
        return Objects.equals(getUsername(), connect.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsername());
    }
}
