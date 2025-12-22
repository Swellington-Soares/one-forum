package br.one.forum.service;


import br.one.forum.configuration.JwtProperties;
import br.one.forum.dto.JwtTokenDto;
import br.one.forum.entity.Token;
import br.one.forum.entity.User;
import br.one.forum.exception.api.TokenNotFoundException;
import br.one.forum.exception.api.TokenVerificationException;
import br.one.forum.repository.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtProperties jwtProperties;

//    @Value("${jwt.issuer}")
//    private String jwtIssuer;
//
//    @Value("${jwt.access-token.key}")
//    private String accessTokenKey;
//
//    @Value("${jwt.access-token.expiration}")
//    private int accessTokenExpiration;
//
//    @Value("${jwt.refresh-token.key}")
//    private String refreshTokenKey;
//
//    @Value("${jwt.refresh-token.expiration}")
//    private int refreshTokenExpiration;

    private String _generateToken(User user, String secret, Instant expirationIn) {
        return JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(expirationIn)
                .sign(Algorithm.HMAC256(secret));
    }

    private DecodedJWT _validateToken(String token, String secret) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(jwtProperties.getIssuer())
                .build()
                .verify(token);
    }

    public JwtTokenDto generateAccessToken(User user) {
        var expirationDate = getTokenExpirationDate(jwtProperties.getAccessToken().expiration());
        var token = _generateToken(user, jwtProperties.getAccessToken().key(), expirationDate);
        return new JwtTokenDto(token, expirationDate);
    }

    public JwtTokenDto generateRefreshToken(User user) {
        var expirationDate = getTokenExpirationDate(jwtProperties.getRefreshToken().expiration());
        var token = _generateToken(user, jwtProperties.getRefreshToken().key(), expirationDate);
        return new JwtTokenDto(token, expirationDate);
    }



    public DecodedJWT validateRefreshToken(String refreshToken) {
        return _validateToken(refreshToken, jwtProperties.getRefreshToken().key());
    }

    public DecodedJWT validateAccessToken(String token) {
        return _validateToken(token, jwtProperties.getAccessToken().key());
    }

    private Instant getTokenExpirationDate(int expirationMinutes) {
        return LocalDateTime.now().plusMinutes(expirationMinutes).toInstant(ZoneOffset.of("-03:00"));
    }

    @Transactional
    public Token generateEmailToken(String email, Token.TokenType type) {
        var tokenStr = UUID.randomUUID().toString();
        var currentToken = tokenRepository.findByEmailAndType(email, type).orElse(null);

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


    public String extractTokenFromRequest(HttpServletRequest request) {
        var  header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer")) {
            return header.replace("Bearer", "").trim();
        }
        return null;
    }

    @Transactional
    public void deleteEmailToken(String token) {
        tokenRepository.deleteByTokenIgnoreCase(token);
    }

}
