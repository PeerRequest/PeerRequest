package com.peerrequest.app;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * A Security Configuration for Spring Security guarding all endpoints behind authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    Environment env;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .headers()
            .frameOptions()
            .sameOrigin();

        http
            .csrf()
            .disable();

        http
            .authorizeHttpRequests((authorize) -> {
                if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
                    authorize.requestMatchers("/test/**").permitAll();
                }

                authorize
                    .requestMatchers("/error").permitAll()
                    .requestMatchers(LogoutController.LOGOUT_URL).permitAll()
                    .anyRequest().authenticated();
            })
            .oauth2Login((login) -> login
                .successHandler(new RefererRedirectionAuthenticationSuccessHandler())
            )
            .oauth2Client(withDefaults());

        var map = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
        map.put(new AntPathRequestMatcher("/api"), new Http403ForbiddenEntryPoint());
        map.put(new AntPathRequestMatcher("/api/**"), new Http403ForbiddenEntryPoint());
        map.put(new AntPathRequestMatcher("/**"),
            new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/keycloak"));

        http
            .exceptionHandling()
            .authenticationEntryPoint(new DelegatingAuthenticationEntryPoint(map));


        // @formatter:on
        return http.build();
    }

    static class RefererRedirectionAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

        public RefererRedirectionAuthenticationSuccessHandler() {
            super();
            setUseReferer(true);
        }
    }
}