package br.one.forum.dtos;

import java.io.Serializable;
import java.time.Instant;

public record UserAuthResponseDto(
        int id,
        Instant createdAt,
        String email,
        String profileName,
        String profilePhoto,
        String profileBio) implements Serializable {
}
