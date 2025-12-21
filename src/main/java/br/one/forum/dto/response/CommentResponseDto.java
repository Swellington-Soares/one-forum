package br.one.forum.dto.response;

import java.time.Instant;

public record CommentResponseDto(
        Long id,
        Long topicId,
        Long userId,
        String userProfileName,
        String userProfilePhoto,
        String content,
        Instant createdAt,
        Instant updateAt
) {
}
