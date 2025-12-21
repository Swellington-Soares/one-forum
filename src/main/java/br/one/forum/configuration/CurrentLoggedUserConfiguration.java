package br.one.forum.configuration;

import br.one.forum.entity.CurrentUser;
import br.one.forum.entity.User;
import br.one.forum.infra.security.AppUserDetailsInfo;
import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
class CurrentLoggedUserConfiguration {

    @Bean(name = "auth")
    @RequestScope
    @Nullable
    public CurrentUser currentLoggedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetailsInfo(User user)))
            return new CurrentUser(null);
        return new CurrentUser(user);
    }
}
