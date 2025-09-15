package booking.service.config;

import booking.service.model.CustomUserPrincipal;
import booking.service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${app.auth.redirect-url}")
    private String redirectBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        log.info("OAuth2 authentication success triggered");
        log.info("Authentication object: {}", authentication);

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof CustomUserPrincipal customUser) {
            email = customUser.getEmail();
            log.info("Extracted email from CustomUserPrincipal: {}", email);

        } else if (principal instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
            log.warn("Using OAuth2User directly, extracted email: {}", email);

        } else {
            log.error("Unexpected principal type: {}",
                    principal != null ? principal.getClass().getName() : "null");
            throw new IllegalStateException("Authentication principal is not recognized");
        }

        String token = jwtUtil.generateToken(email);
        log.info("Generated JWT for {}: {}", email, token);

        String redirectUrl = redirectBaseUrl + "?token=" + token;
        log.info("Redirecting to frontend: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
