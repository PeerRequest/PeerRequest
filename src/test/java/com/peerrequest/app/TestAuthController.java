package com.peerrequest.app;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A Controller to allow login during tests without keycloak.
 */
@RestController
@RequestMapping("/test/auth")
public class TestAuthController {
    /**
     * Defines an endpoints to set user information and perform login during tests.
     *
     * @param req        current http request
     * @param userId     user id for test user
     * @param userName   user name id for test user
     * @param givenName  given name id for test user
     * @param familyName family name id for test user
     * @param email      email id for test user
     */
    @GetMapping("/login")
    public void login(HttpServletRequest req,
                      @RequestParam("user_id") String userId,
                      @RequestParam("user_name") String userName,
                      @RequestParam("given_name") String givenName,
                      @RequestParam("family_name") String familyName,
                      @RequestParam("email") String email
    ) {
        var token = new OAuth2AuthenticationToken(
            new OAuth2User() {
                @Override
                public Map<String, Object> getAttributes() {
                    return Map.of(
                        "sub", userId,
                        "preferred_username", userName,
                        "given_name", givenName,
                        "name", givenName + " " + familyName,
                        "family_name", familyName,
                        "email", email
                    );
                }

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return List.of();
                }

                @Override
                public String getName() {
                    return userName;
                }
            },
            List.of(),
            "test-auth"
        );

        var securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }
}
