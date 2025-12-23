package br.one.forum.dto.response;

import java.time.Instant;
import java.util.List;

public record TopicResponseDto(
        Long id,
        String title,
        Long likes,
        Long commentCount,
        String content,
        boolean likedByCurrentUser,
        UserInfoResponseDto profile,
        Instant createdAt,
        Instant updatedAt,
        List<String> categories) {
}
