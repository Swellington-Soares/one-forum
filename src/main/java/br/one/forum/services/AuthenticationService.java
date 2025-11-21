package br.one.forum.services;

import br.one.forum.dtos.AuthenticationRequestDto;
import br.one.forum.dtos.LoginResponseDto;
import br.one.forum.entities.User;
import br.one.forum.exception.RefreshTokenInvalidException;
import br.one.forum.exception.ApiTokenExpiredException;
import br.one.forum.exception.UserNotFoundException;
import br.one.forum.exception.UserPasswordNotMatchException;
import br.one.forum.security.AppUserDetails;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public LoginResponseDto login(AuthenticationRequestDto data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        Authentication auth = authenticationManager.authenticate(userNamePassword);

        AppUserDetails appUserDetails = (AppUserDetails) auth.getPrincipal();
        User user = appUserDetails.getUser();

        if (user == null) {
            throw new UserNotFoundException(data.email());
        }
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new UserPasswordNotMatchException();
        }

        var accessToken = tokenService.generateToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);

        return new LoginResponseDto(accessToken.token(), refreshToken.token());
    }

    public LoginResponseDto refreshToken(@NotBlank String refreshToken) {
        DecodedJWT decodedJWT = tokenService.validateRefreshToken(refreshToken);
        String email = decodedJWT.getSubject();
        User user = userService.findUserByEmail(email, false);

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenInvalidException();
        }

        if (user.getRefreshTokenExpiration().isBefore(Instant.now())) {
            throw new ApiTokenExpiredException();
        }

        var newAccessToken = tokenService.generateToken(user);
        var newRefreshToken = tokenService.generateRefreshToken(user);
        userService.updateRefreshToken(user, newRefreshToken);

        return new LoginResponseDto(
                newAccessToken.token(),
                newRefreshToken.token()
        );
    }
}
