package br.one.forum.service;

import br.one.forum.dto.request.LoginRequestDto;
import br.one.forum.dto.response.LoginResponseDto;
import br.one.forum.entity.Token;
import br.one.forum.entity.User;
import br.one.forum.exception.api.RefreshTokenExpiredException;
import br.one.forum.exception.api.RefreshTokenInvalidException;
import br.one.forum.exception.api.UserAccountAlreadyVerified;
import br.one.forum.infra.security.AppUserDetailsInfo;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${api.base-url}")
    private String apiBaseUrl = "";

    public LoginResponseDto login(LoginRequestDto data) {
        var loginToken = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        Authentication authentication = authenticationManager.authenticate(loginToken);
        AppUserDetailsInfo info = (AppUserDetailsInfo) authentication.getPrincipal();

        User user = info.user();
        var accessToken = tokenService.generateAccessToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);
        userService.updateUserRefreshToken(user, refreshToken);
        return new LoginResponseDto(accessToken.token(), refreshToken.token());
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        DecodedJWT decodedJWT = tokenService.validateRefreshToken(refreshToken);
        String email = decodedJWT.getSubject();
        User user = userService.findUserByEmail(email);
        if (!refreshToken.equals(user.getRefreshToken()))
            throw new RefreshTokenInvalidException();

        if (user.getRefreshTokenExpiration().isBefore(Instant.now()))
            throw new RefreshTokenExpiredException();

        var newAccessToken = tokenService.generateAccessToken(user);
        var newRefreshToken = tokenService.generateRefreshToken(user);
        userService.updateUserRefreshToken(user, newRefreshToken);
        return new LoginResponseDto(newAccessToken.token(), newRefreshToken.token());
    }

    public void requestConfirmationAccountToken(String email) {
        var user = userService.findUserByEmail(email);
        if (user.isEmailVerified())
            throw new UserAccountAlreadyVerified();

        var requestToken = tokenService.generateEmailToken(user.getEmail(), Token.TokenType.EMAIL_TOKEN);
        var link = apiBaseUrl + "/" + requestToken.getToken();

        emailService.sendHtmlMessage(
                user.getEmail(),
                "Ativar conta",
                "confirm.html",
                Map.of("link", link)
        );
    }

}
