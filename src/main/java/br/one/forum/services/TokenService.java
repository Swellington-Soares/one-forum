package br.one.forum.services;

import br.one.forum.dtos.TokenDto;
import br.one.forum.entities.EmailToken;
import br.one.forum.entities.User;
import br.one.forum.repositories.EmailTokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    private final EmailTokenRepository emailTokenRepository;
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

    public String generateEmailToken(String email) {
        var token = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        var expirationDate = getTokenExpirationDate(120);
        var tokenType = EmailToken.TokenType.EMAIL_TOKEN;
        var tokenEntity = emailTokenRepository.findByEmailAndType(email, tokenType).orElse(null);

        if (tokenEntity != null && (!tokenEntity.isExpired())) {
            return tokenEntity.getToken();
        }

        if (tokenEntity == null || tokenEntity.isExpired()) {

            if (tokenEntity != null) {
                emailTokenRepository.delete(tokenEntity);
            }

            emailTokenRepository.save(EmailToken.builder()
                    .email(email)
                    .expiration(expirationDate)
                    .token(token)
                    .type(tokenType)
                    .build());
        }


        return token;
    }

}
