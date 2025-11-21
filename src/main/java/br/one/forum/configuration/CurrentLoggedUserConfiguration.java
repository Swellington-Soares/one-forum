package br.one.forum.configuration;

import br.one.forum.security.AppUserDetails;
import br.one.forum.services.AuthenticationService;
import br.one.forum.services.CurrentUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class CurrentLoggedUserConfiguration {

    @Bean
    @RequestScope
    @Nullable
    public CurrentUser currentLoggedUser(AuthenticationService authenticationService) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails userDetails))
            return new CurrentUser(null);
        return new CurrentUser(userDetails.getUser());
    }
}
