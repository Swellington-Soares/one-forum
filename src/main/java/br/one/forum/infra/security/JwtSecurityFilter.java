package br.one.forum.infra.security;

import br.one.forum.services.AppUserDetailsService;
import br.one.forum.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = tokenService.extractTokenFromRequest(request);
        if (token != null) {
            try {
                var decodedToken = tokenService.validateToken(token);
                var login = decodedToken.getSubject();
                var user = userDetailsService.loadUserByUsername(login);
            } catch (Exception ignored) {}
        }
        filterChain.doFilter(request, response);
    }
}
