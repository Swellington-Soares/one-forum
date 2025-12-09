package br.one.forum.services;

import br.one.forum.dtos.ConfirmAccountResponseDto;
import br.one.forum.dtos.request.AuthenticationRequestDto;
import br.one.forum.dtos.response.LoginResponseDto;
import br.one.forum.entities.Token;
import br.one.forum.entities.User;
import br.one.forum.exception.api.UserAccountAlreadyVerified;
import br.one.forum.exception.api.ApiTokenExpiredException;
import br.one.forum.exception.api.RefreshTokenInvalidException;
import br.one.forum.exception.api.UserNotFoundException;
import br.one.forum.exception.api.UserPasswordNotMatchException;
import br.one.forum.security.AppUserDetails;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${api-base}")
    private final String apiBaseUrl = "";

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final EmailService emailService;

    public LoginResponseDto login(AuthenticationRequestDto data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        Authentication auth = authenticationManager.authenticate(userNamePassword);

        AppUserDetails appUserDetails = (AppUserDetails) auth.getPrincipal();
        User user = appUserDetails.user();

        if (user == null) {
            throw new UserNotFoundException(data.email());
        }
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new UserPasswordNotMatchException();
        }

        var accessToken = tokenService.generateToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);

        userService.updateRefreshToken(user, refreshToken);

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

    public ConfirmAccountResponseDto requestConfirmAccount(String email)  {
            try {
                var user = userService.findUserByEmail(email, false);
                if (user.isEmailVerified())
                    throw new UserAccountAlreadyVerified();

                var requestToken = tokenService.generateEmailToken(email, Token.TokenType.EMAIL_TOKEN);

                var link = apiBaseUrl + "/" + requestToken.getToken();

                emailService.sendHtmlMessage(
                        user.getEmail(),
                        "Ativar conta",
                        "confirm",
                        Map.of("link", link)
                );
            } catch (Exception e) {
                return new ConfirmAccountResponseDto(400, e.getMessage());
            }

            return new ConfirmAccountResponseDto(200, "OK");

    }
}
