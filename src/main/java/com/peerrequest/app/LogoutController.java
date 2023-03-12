package com.peerrequest.app;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
    String logout(HttpServletRequest request, HttpServletResponse response, @RequestHeader String host) {
        var redirect = "forward:/";

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken token) {
            var oauth2User = token.getPrincipal();

            var logoutRedirect = URLEncoder.encode(request.getScheme() + "://" + host, StandardCharsets.UTF_8);
            var clientId =
                URLEncoder.encode(Objects.requireNonNull(oauth2User.getAttribute("azp")), StandardCharsets.UTF_8);
            redirect = "redirect:" + oauth2User.getAttribute("iss")
                + "/protocol/openid-connect/logout"
                + "?post_logout_redirect_uri=" + logoutRedirect
                + "&client_id=" + clientId;
        }

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

        return redirect;
    }
}
