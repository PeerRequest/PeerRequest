package com.peerrequest.app.interceptors;

import com.peerrequest.app.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * An Interceptor including user data for the current user as response headers.
 */
public class UserCookieInterceptor implements HandlerInterceptor {
    public static final String COOKIE = "current-user";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @Nullable Object handler) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken token) {
            var oauth2User = token.getPrincipal();
            var user =
                new User(new User.UserSelector(oauth2User.getAttribute("sid")), oauth2User.getAttribute("given_name"),
                    oauth2User.getAttribute("family_name"), oauth2User.getAttribute("email"));

            var json = user.toJson();
            json.put("account_management_url", oauth2User.getAttribute("iss") + "/account");

            var cookie =
                new Cookie(COOKIE, URLEncoder.encode(json.toJSONString(), StandardCharsets.UTF_8));
            cookie.setPath("/");
            cookie.setHttpOnly(false);
            cookie.setSecure(true);
            cookie.setMaxAge(-1);

            response.addCookie(cookie);
        }

        return true;
    }
}