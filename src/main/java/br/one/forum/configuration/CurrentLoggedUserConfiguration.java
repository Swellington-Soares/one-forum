package br.one.forum.configuration;

import br.one.forum.entities.CurrentUser;
import br.one.forum.security.AppUserDetails;
import br.one.forum.services.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class CurrentLoggedUserConfiguration {

    @Bean(name = "auth")
    @RequestScope
    @Nullable
    public CurrentUser currentLoggedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails(br.one.forum.entities.User user)))
            return new CurrentUser(null);
        return new CurrentUser(user);
    }
}
