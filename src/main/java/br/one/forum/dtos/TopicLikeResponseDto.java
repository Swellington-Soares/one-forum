package br.one.forum.dtos;

public record TopicLikeResponseDto(
        int topicId,
        int likeCount
) {
}
