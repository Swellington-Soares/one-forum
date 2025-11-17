package br.one.forum.services;

import br.one.forum.entities.User;
import br.one.forum.exception.AuthenticationCredentialException;
import br.one.forum.repositories.UserRepository;
import br.one.forum.security.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
            return new UserSecurityDetails(user);
        } catch (Exception e) {
            throw new AuthenticationCredentialException();
        }
    }
}
