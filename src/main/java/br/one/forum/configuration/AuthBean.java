package br.one.forum.configuration;

import br.one.forum.entities.User;
import br.one.forum.services.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class AuthBean {

    @Bean
    @RequestScope
    public User currentLoggedUser(AuthenticationService authenticationService) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails userDetails))
            return null;
        return authenticationService.getLoggedUserByUserDetails(userDetails);
    }
}
