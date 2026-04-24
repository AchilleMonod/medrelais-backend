package vbm.medrelais.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.Role;
import vbm.medrelais.port.UtilisateurRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    // =========================================================
    // loadUserByUsername
    // =========================================================

    @Test
    void loadUserByUsername_whenFound_shouldReturnUserDetailsWithCorrectUsernameAndRole() {
        // GIVEN
        PraticienBO praticien = PraticienBO.builder()
                .id(1L)
                .email("praticien@test.com")
                .password("encodedPassword")
                .role(Role.PRATICIEN)
                .build();
        when(utilisateurRepository.findByEmail("praticien@test.com"))
                .thenReturn(Optional.of(praticien));

        // WHEN
        UserDetails result = userDetailsService.loadUserByUsername("praticien@test.com");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("praticien@test.com");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        // Le rôle doit être préfixé ROLE_
        assertThat(result.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_PRATICIEN"));
        verify(utilisateurRepository).findByEmail("praticien@test.com");
    }

    @Test
    void loadUserByUsername_whenNotFound_shouldThrowUsernameNotFoundException() {
        // GIVEN
        when(utilisateurRepository.findByEmail("inconnu@test.com"))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("inconnu@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("inconnu@test.com");
        verify(utilisateurRepository).findByEmail("inconnu@test.com");
    }

    @Test
    void loadUserByUsername_shouldWrapBoInUserDetailsAdapter() {
        // GIVEN — vérifie que le résultat est bien un UserDetailsAdapter
        PraticienBO praticien = PraticienBO.builder()
                .id(2L)
                .email("admin@test.com")
                .password("secret")
                .role(Role.PRATICIEN)
                .build();
        when(utilisateurRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(praticien));

        // WHEN
        UserDetails result = userDetailsService.loadUserByUsername("admin@test.com");

        // THEN
        assertThat(result).isInstanceOf(UserDetailsAdapter.class);
        UserDetailsAdapter adapter = (UserDetailsAdapter) result;
        assertThat(adapter.getUtilisateurBO().getId()).isEqualTo(2L);
    }
}

