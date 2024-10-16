package model;

public class UserData {
    private String username;
    private String password;
    private String email;

    UserData(String username) {
        this.username = username;
    }

    String getUsername() {
        return username;
    }
}
