package booking.service.config;

import booking.service.model.Role;
import booking.service.model.RoleName;
import booking.service.model.User;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<Role> userRoles = Arrays.stream(customUser.roles())
                .map(roleStr -> {
                    Role role = new Role();
                    role.setName(RoleName.valueOf(roleStr));
                    return role;
                })
                .collect(Collectors.toSet());

        User user = new User()
                .setId(customUser.id())
                .setEmail(customUser.username())
                .setPassword("password")
                .setRoles(userRoles);

        List<GrantedAuthority> authorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user,
                        "password",
                        authorities
                );

        context.setAuthentication(auth);
        return context;
    }
}
