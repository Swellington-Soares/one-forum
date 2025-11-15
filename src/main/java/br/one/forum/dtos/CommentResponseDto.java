package br.one.forum.dtos;

import java.time.Instant;

public record CommentResponseDto(
        Integer id,
        Integer topicId,
        Integer userId,
        String content,
        Instant createdAt
) {}

