package br.one.forum.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "tokens")
public class Token extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(unique = true)
    private String token;


    @Enumerated(EnumType.STRING)
    private TokenType type = TokenType.EMAIL_TOKEN;


    private Instant expiration;


    public boolean isExpired() {
        return expiration == null || expiration.isBefore(Instant.now());
    }

    public enum TokenType {
        EMAIL_TOKEN,
        PASSWORD_TOKEN
    }

    @Builder
    public Token(String email, String token, TokenType type, Instant expiration) {
        this.email = email;
        this.token = token;
        this.type = type;
        this.expiration = expiration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return Objects.equals(token, token1.token);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(token);
    }
}
