package de.szut.pms.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenForwardingTest {

    private TokenForwarding tokenForwarding;

    @BeforeEach
    void setUp() {
        tokenForwarding = new TokenForwarding();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Clear the security after test
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsTokenJwt() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("sub", "john")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        Assertions.assertEquals("test-token", tokenForwarding.getToken());
    }

    @Test
    void throwsNoAuthentication() {
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> tokenForwarding.getToken());

        Assertions.assertEquals("Authentication is null", ex.getMessage());
    }

    @Test
    void throwsNotJwt() {
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("john", "password"));

        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> tokenForwarding.getToken());

        Assertions.assertEquals("Principal is not Jwt", ex.getMessage());
    }
}