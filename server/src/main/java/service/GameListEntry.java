package service;

public class GameListEntry {
    private final Integer gameID;
    private final String gameName;
    private final String whiteUsername;
    private final String blackUsername;

    public GameListEntry(Integer gameId, String gameName, String whiteUsername, String blackUsername) {
        this.gameID = gameId;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
    }

    // Getters
    public Integer getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }
}

