package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

public class Connect extends UserGameCommand {
    private final String username;

    public Connect(String authToken, Integer gameID, String username) {
        super(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.username = username;
    }

    public String getUsername() {
        return username;
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
