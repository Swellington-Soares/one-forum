package br.one.forum;

import br.one.forum.entities.Profile;
import br.one.forum.entities.User;
import br.one.forum.security.AppUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestUtils {

    public static User mockAuthenticatedUser(int id, String name) {
        User user = new User();
        user.setEmail("authuser@dev.com");
        user.setId(id);
        user.setPassword("{noop}123456");
        user.setProfile(Profile.builder().name(name).build());
        AppUserDetails userDetails = new AppUserDetails(user);
        var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        return user;
    }

    public static void clearAuth() {
        SecurityContextHolder.clearContext();
    }


}
