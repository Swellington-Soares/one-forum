package br.one.forum.dtos;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link br.one.forum.entities.User}
 */
public record UserPublicResponseDto(
        int id,
        Instant createdAt,
        String profileName,
        String profilePhoto,
        String profileBio) implements Serializable {
}