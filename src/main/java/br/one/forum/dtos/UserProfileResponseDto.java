package br.one.forum.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link br.one.forum.entities.User}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileResponseDto(
        int id,
        int topicCreatedCount,
        int commentsCount,
        String email,
        Instant createdAt,
        Instant updateAt,
        String profileName, String profilePhoto,
        String profileBio) implements Serializable {
}