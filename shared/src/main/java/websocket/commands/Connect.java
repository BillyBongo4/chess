package websocket.commands;

import chess.ChessGame;

import java.util.Objects;

public class Connect extends UserGameCommand {
    private final ChessGame game;

    public Connect(CommandType commandType, String authToken, Integer gameID, ChessGame game) {
        super(commandType, authToken, gameID);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
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
        return Objects.equals(getGame(), connect.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGame());
    }
}
