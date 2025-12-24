package br.one.forum.dto;

import java.time.Instant;

public record JwtTokenDto(
        String token,
        Instant expirationDate,
        Instant createdAt
) {
}
