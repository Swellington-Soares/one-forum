package br.one.forum.configuration;

import br.one.forum.infra.security.JwtSecurityFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfiguration {

    private final JwtSecurityFilter jwtSecurityFilter;

    private final String[] ALLOWED_ROUTES = {
            "/error",
            "/avatars/**",
            "/auth/**",
            "/actuator/**",
            "/topics/**",
            "/categories/**",
            "/users/{id}",
            "/users/{id}/**",
            "/users/register",
            "/profile/**",
            "/auth/request-password-change",
            "/auth/change-password/**",
            "/favicon.ico",
            // Swagger/OpenAPI
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                //.csrf(AbstractHttpConfigurer::disable)
                .csrf(csrf -> csrf.requireCsrfProtectionMatcher(
                        (RequestMatcher) request -> request.getMethod().equals("GET") &&
                                request.getRequestURI().startsWith("/auth/change-password/")
                ))
                .sessionManagement(
                        s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        a -> a.requestMatchers(ALLOWED_ROUTES).permitAll()

                                .anyRequest().authenticated()
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
