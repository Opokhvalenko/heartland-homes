package booking.service.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomUserPrincipal implements OAuth2User, UserDetails {

    private final User user;
    private final OAuth2User oauth2User; // може бути DefaultOidcUser чи DefaultOAuth2User

    public CustomUserPrincipal(User user, OAuth2User oauth2User) {
        this.user = user;
        this.oauth2User = oauth2User;
    }

    // --- UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return !user.isDeleted();
    }

    // --- OAuth2User ---
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    // --- Допоміжні методи ---
    public String getEmail() {
        return user.getEmail();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public Map<String, Object> getClaims() {
        if (oauth2User instanceof OidcUser oidcUser) {
            return oidcUser.getClaims();
        }
        return Collections.emptyMap();
    }
}
