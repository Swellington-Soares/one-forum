package br.one.forum.services;

import br.one.forum.exception.AuthenticationCredentialException;
import br.one.forum.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new AppUserDetails(userService.findUserByEmail(username, false));
        } catch (Exception e) {
            throw new AuthenticationCredentialException();
        }
    }
}
