package br.one.forum.service;

import br.one.forum.exception.api.UserNotFoundException;
import br.one.forum.infra.security.AppUserDetailsInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws  UsernameNotFoundException {
        try {
            var user = userService.findUserByEmail(username);
            return new AppUserDetailsInfo(user);
        } catch (Exception e) {
            if (e instanceof UserNotFoundException) {
                throw new UsernameNotFoundException("Wrong email or password");
            } else {
                throw e;
            }
        }
    }
}
