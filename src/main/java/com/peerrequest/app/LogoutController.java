package com.peerrequest.app;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to allow users to logout.
 */
@Controller
public class LogoutController {
    public static final String LOGOUT_URL = "/logout";

    @GetMapping(LOGOUT_URL)
    String logout(HttpServletRequest request, HttpServletResponse response) {
        var redirect = "forward:/";

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken token) {
            var oauth2User = token.getPrincipal();
            redirect = "redirect:" + oauth2User.getAttribute("iss") + "/protocol/openid-connect/logout";
        }

        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }

        return redirect;
    }
}
