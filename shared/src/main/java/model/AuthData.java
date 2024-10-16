package model;

import java.util.UUID;

public class AuthData {
    private String authToken;
    private String username;

    public AuthData(String username) {
        authToken = UUID.randomUUID().toString();
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
