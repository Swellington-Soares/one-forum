package br.one.forum.services;

import br.one.forum.dtos.TokenDto;
import br.one.forum.entities.Token;
import br.one.forum.entities.User;
import br.one.forum.exception.api.TokenNotFoundException;
import br.one.forum.exception.api.TokenVerificationException;
import br.one.forum.repositories.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Value("${api.security.access-token.key}")
    private String tokenSecretKey;

    @Value("${api.security.access-token.expiration}")
    private int tokenExpirationInMinutes;

    @Value("${api.security.refresh-token.key}")
    private String refreshTokenSecretKey;

    @Value("${api.security.refresh-token.expiration}")
    private int refreshTokenExpirationInMinutes;

    private String _generateToken(User user, String secret, Instant expirationIn) {
        return JWT.create()
                .withIssuer("auth-api")
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(expirationIn)
                .sign(Algorithm.HMAC256(secret));
    }

    public TokenDto generateToken(User user) {
        var expirationDate = getTokenExpirationDate(tokenExpirationInMinutes);
        var token = _generateToken(user, tokenSecretKey, expirationDate);
        return new TokenDto(token, expirationDate);
    }

    public TokenDto generateRefreshToken(User user) {
        var expirationDate = getTokenExpirationDate(refreshTokenExpirationInMinutes);
        var token = _generateToken(user, refreshTokenSecretKey, expirationDate);
        return new TokenDto(token, expirationDate);
    }

    private DecodedJWT _validateToken(String token, String secret) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("auth-api")
                .build()
                .verify(token);
    }

    public DecodedJWT validateRefreshToken(String refreshToken) {
        return _validateToken(refreshToken, refreshTokenSecretKey);
    }

    public DecodedJWT validateToken(String token) {
        return _validateToken(token, tokenSecretKey);
    }

    private Instant getTokenExpirationDate(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes).toInstant(ZoneOffset.of("-03:00"));
    }

    public Token generateEmailToken(String email, Token.TokenType type) {
        var tokenStr = UUID.randomUUID().toString();
        var currentToken = tokenRepository.findByEmailAndType(email, type)
                .orElse(null);
        if (currentToken != null && currentToken.isExpired()) {
            tokenRepository.delete(currentToken);
        }

        if (currentToken != null && !currentToken.isExpired()) {
            return currentToken;
        }

        currentToken = Token.builder()
                .email(email)
                .expiration(getTokenExpirationDate(60))
                .type(type)
                .token(tokenStr).build();

        return tokenRepository.save(currentToken);
    }

    public Token validateEmailToken(String tokenStr) {
        var token = tokenRepository.findByToken(tokenStr).orElseThrow(TokenNotFoundException::new);
        if (token.isExpired()) throw new TokenVerificationException();
        return token;
    }

    public void deleteToken(@NotNull String token) {
        tokenRepository.deleteByTokenIgnoreCase(token);
    }

    public Token findTokenByEmail(String email) {
        return null;
    }
}
