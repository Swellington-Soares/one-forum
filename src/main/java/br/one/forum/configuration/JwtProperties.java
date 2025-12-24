package br.one.forum.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter @Setter
public class JwtProperties {

    private String issuer = "FORUM_JWT_ONE";
    private TokenConfig accessToken = new TokenConfig("JWT_KEY", 5);
    private TokenConfig refreshToken = new TokenConfig("JWT_KEY", 150);

    public record TokenConfig(
            String key,
            int expiration
    ) {
    }
}
