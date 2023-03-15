package com.peerrequest.app;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to allow users to logout.
 */
@Controller
public class LogoutController {
    public static final String LOGOUT_URL = "/logout/";

    @RequestMapping(LOGOUT_URL)
    ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response,
                                  @RequestHeader String host) {
        var redirect = "/";

        final var auth = SecurityContextHolder.getContext().getAuthentication();

        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
            }
        }

        if (auth instanceof OAuth2AuthenticationToken token) {
            var oauth2User = token.getPrincipal();

            var logoutRedirect = URLEncoder.encode(request.getScheme() + "://" + host, StandardCharsets.UTF_8);

            var azp = oauth2User.getAttribute("azp");
            if (azp != null) {
                var clientId =
                    URLEncoder.encode(azp.toString(), StandardCharsets.UTF_8);
                redirect = oauth2User.getAttribute("iss")
                    + "/protocol/openid-connect/logout"
                    + "?post_logout_redirect_uri=" + logoutRedirect
                    + "&client_id=" + clientId;
            } else {
                return ResponseEntity.ok("Logged out");
            }
        }

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirect);
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
    }
}
