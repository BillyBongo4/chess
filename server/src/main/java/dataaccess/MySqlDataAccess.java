package dataaccess;

import model.*;

import java.sql.SQLException;

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
                "password VARCHAR(50) NOT NULL, " +
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
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public AuthData createAuth(AuthData authData) throws Exception {
        String query = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return new GameData[0];
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean checkColorUsername(int gameID, String color) throws DataAccessException {
        return false;
    }

    @Override
    public void updateGame(int gameID, String username, String color) throws DataAccessException {

    }

    @Override
    public void clearGameData() {

    }

    @Override
    public void clearAuthData() {

    }

    @Override
    public void clearUserData() {

    }
}
