package vbm.medrelais.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    // Clé Base64 valide de 32 octets minimum pour HS256
    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000L; // 24h

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);

        userDetails = new User("user@test.com", "password", Collections.emptyList());
    }

    // ─── generateToken ────────────────────────────────────────────────────────

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_shouldContainThreeParts() {
        // Un JWT valide a 3 parties séparées par des points : header.payload.signature
        String token = jwtService.generateToken(userDetails);
        assertThat(token.split("\\.")).hasSize(3);
    }

    // ─── extractUsername ──────────────────────────────────────────────────────

    @Test
    void extractUsername_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("user@test.com");
    }

    // ─── isTokenValid ─────────────────────────────────────────────────────────

    @Test
    void isTokenValid_withCorrectUser_shouldReturnTrue() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_shouldReturnFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("autre@test.com", "password", Collections.emptyList());
        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() {
        // On configure une expiration à -1ms → token déjà expiré à la génération
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }



    // ─── token invalide ───────────────────────────────────────────────────────

    @Test
    void extractUsername_withMalformedToken_shouldThrowException() {
        assertThatThrownBy(() -> jwtService.extractUsername("token.invalide.xxx"))
                .isInstanceOf(Exception.class);
    }
}

