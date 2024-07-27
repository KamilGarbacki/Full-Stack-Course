package com.kgarbacki.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
