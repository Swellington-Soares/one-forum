package br.one.forum.dto.response;

import java.time.Instant;

public record UserProfileResponseDto(
        Long id,
        Long topicCreatedCount,
        Long commentsCount,
        String email,
        Instant createdAt,
        Instant updateAt,
        String profileName,
        String profilePhoto,
        String profileBio
) {

}
