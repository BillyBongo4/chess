package model;

import java.util.UUID;

public class AuthData {
    private String username;
    private String authToken;

    public AuthData(String username) {
        this.username = username;
        authToken = UUID.randomUUID().toString();
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
