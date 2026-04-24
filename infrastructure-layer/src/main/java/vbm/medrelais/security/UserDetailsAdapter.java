package vbm.medrelais.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import vbm.medrelais.model.UtilisateurBO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsAdapter implements UserDetails {

    private final UtilisateurBO utilisateurBO;

    public UserDetailsAdapter(UtilisateurBO utilisateurBO) {
        this.utilisateurBO = utilisateurBO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + utilisateurBO.getRole().name()));
    }

    @Override
    public String getPassword() {
        return utilisateurBO.getPassword();
    }

    @Override
    public String getUsername() {
        return utilisateurBO.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

