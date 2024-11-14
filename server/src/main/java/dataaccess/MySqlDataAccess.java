package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() {
        try {
            DatabaseManager.createDatabase();
            createTables();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() throws Exception {
        String userDataTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(50) NOT NULL PRIMARY KEY, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100) NOT NULL)";
        String authDataTable = "CREATE TABLE IF NOT EXISTS auths (" +
                "authToken VARCHAR(255) NOT NULL PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL)";
        String gameDataTable = "CREATE TABLE IF NOT EXISTS games (" +
                "gameID INT AUTO_INCREMENT PRIMARY KEY, " +
                "gameName VARCHAR(100) NOT NULL, " +
                "whiteUsername VARCHAR(50), " +
                "blackUsername VARCHAR(50), " +
                "chessGame TEXT NOT NULL)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.createStatement()) {
                statement.executeUpdate(userDataTable);
                statement.executeUpdate(authDataTable);
                statement.executeUpdate(gameDataTable);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws Exception {
        String query = "SELECT * FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                var result = preparedStatement.executeQuery();
                if (result.next()) {
                    return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();

                return userData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        String query = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();

                return authData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String query = "SELECT * FROM auths WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authToken);
                var result = preparedStatement.executeQuery();
                if (result.next()) {
                    return new AuthData(result.getString("authToken"), result.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String query = "DELETE FROM auths WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        String query = "SELECT * FROM games";
        List<GameData> gamesList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                var result = preparedStatement.executeQuery();
                while (result.next()) {
                    int gameId = result.getInt("gameID");
                    String gameName = result.getString("gameName");
                    String whiteUsername = result.getString("whiteUsername");
                    String blackUsername = result.getString("blackUsername");
                    String chessGameJson = result.getString("chessGame");
                    ChessGame game = new Gson().fromJson(chessGameJson, ChessGame.class);

                    GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);

                    gamesList.add(gameData);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gamesList.toArray(new GameData[0]);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        String query = "INSERT INTO games (gameName, whiteUsername, blackUsername, chessGame) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                String chessGame = new Gson().toJson(new ChessGame());

                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, chessGame);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return new GameData(listGames().length, null, null, gameName, new ChessGame());
    }

    @Override
    public boolean checkColorUsername(int gameID, String color) throws DataAccessException {
        String query = "SELECT * FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, gameID);
                var result = preparedStatement.executeQuery();

                if (result.next()) {
                    if (color.equals("WHITE") && result.getString("whiteUsername") != null) {
                        return true;
                    } else if (color.equals("BLACK") && result.getString("blackUsername") != null) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return false;
    }

    @Override
    public void updateGame(int gameID, String username, String color) throws DataAccessException {
        if (gameID > listGames().length) {
            throw new DataAccessException("Error: Invalid gameID");
        } else if (username.isEmpty()) {
            throw new DataAccessException("Error: Invalid username");
        } else if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new DataAccessException("Error: Invalid color");
        }

        String query = "UPDATE games SET " + (color.equalsIgnoreCase("WHITE") ? "whiteUsername" : "blackUsername") + " = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT * FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, String.valueOf(gameID));
                var result = preparedStatement.executeQuery();
                if (result.next()) {
                    var jsonGame = result.getString("chessGame");
                    var game = new Gson().fromJson(jsonGame, ChessGame.class);
                    var gameName = result.getString("gameName");
                    return new GameData(result.getInt("gameID"), result.getString("whiteUsername"), result.getString("blackUsername"), gameName, game);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearGameData() throws DataAccessException {
        String query = "TRUNCATE TABLE games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        String query = "TRUNCATE TABLE auths";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearUserData() throws DataAccessException {
        String query = "TRUNCATE TABLE users";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
