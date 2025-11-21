package br.one.forum.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    @Column(unique = true)
    private String token;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TokenType type = TokenType.EMAIL_TOKEN;

    @NotNull
    private Instant expiration;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public boolean isExpired() {
        return expiration != null && expiration.isBefore(Instant.now());
    }


    public enum TokenType {
        EMAIL_TOKEN,
        PASSWORD_TOKEN
    }
}
