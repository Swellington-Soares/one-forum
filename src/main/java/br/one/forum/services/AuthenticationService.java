package br.one.forum.services;

import br.one.forum.dtos.AuthenticationRequestDto;
import br.one.forum.dtos.UserRegisterRequestDto;
import br.one.forum.entities.User;
import br.one.forum.exception.UserAlreadyRegisteredException;
import br.one.forum.exception.UserNotFoundException;
import br.one.forum.exception.UserPasswordNotMatchException;
import br.one.forum.repositories.UserRepository;
import br.one.forum.security.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(AuthenticationRequestDto data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        Authentication auth = authenticationManager.authenticate(userNamePassword);

        UserSecurityDetails userSecurityDetails = (UserSecurityDetails) auth.getPrincipal();
        User user = userSecurityDetails.user();

        if (user == null) {
            throw new UserNotFoundException(data.email());
        }
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new UserPasswordNotMatchException();
        }

        return tokenService.generateToken(user);
    }

    public boolean register(UserRegisterRequestDto data) {
        if (userRepository.findUserDetailsByEmail(data.email()) != null) {
            throw new UserAlreadyRegisteredException();
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.email(), encryptedPassword);
        userRepository.save(newUser);
        return true;
    }
}
