package vbm.medrelais.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private UserDetails userDetails;
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String USER_EMAIL = "user@test.com";

    @BeforeEach
    void setUp() {
        // Réinitialiser le SecurityContext entre chaque test
        SecurityContextHolder.clearContext();
        userDetails = new User(USER_EMAIL, "password", Collections.emptyList());
    }

    @Test
    void doFilterInternal_withNoAuthHeader_shouldContinueChainWithoutAuthentication() throws Exception {
        // Aucun header Authorization → le filtre laisse passer sans authentifier
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withNonBearerHeader_shouldContinueChainWithoutAuthentication() throws Exception {
        // Header "Basic ..." → pas un token Bearer, on passe
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withValidToken_shouldSetAuthentication() throws Exception {
        // Token valide → l'utilisateur est authentifié dans le SecurityContext
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(userDetailsService.loadUserByUsername(USER_EMAIL)).thenReturn(userDetails);
        when(jwtService.isTokenValid(VALID_TOKEN, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo(USER_EMAIL);
    }

    @Test
    void doFilterInternal_withInvalidToken_shouldNotSetAuthentication() throws Exception {
        // Token invalide (expiré, mauvaise signature...) → pas d'authentification
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(userDetailsService.loadUserByUsername(USER_EMAIL)).thenReturn(userDetails);
        when(jwtService.isTokenValid(VALID_TOKEN, userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

