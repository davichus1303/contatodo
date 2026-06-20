package com.contatodo.application.dto.response;

/**
 * Response DTO for login data.
 */
public class LoginResponse {

    private String token;
    private UserResponse user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
