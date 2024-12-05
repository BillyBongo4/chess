package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGame extends ServerMessage {
    private ChessGame game;
    private String color;

    public LoadGame(ChessGame game, String color) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.color = color;
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

        LoadGame loadGame = (LoadGame) o;
        return Objects.equals(getGame(), loadGame.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGame());
    }
}
