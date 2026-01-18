package com.odc.hub.auth.service;

import com.odc.hub.user.model.Role;
import com.odc.hub.user.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    JwtService jwtService = new JwtService(
            "super-secret-key-super-secret-key-super-secret-key",
            1000 * 60,
            1000 * 60 * 60
    );

    @Test
    void shouldGenerateValidTokenWithRole() {
        User user = new User();
        user.setId("user123");
        user.setRole(Role.ADMIN);

        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.isTokenValid(token)).isTrue();

        Claims claims = jwtService.extractClaims(token);
        assertThat(claims.getSubject()).isEqualTo("user123");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }
}
