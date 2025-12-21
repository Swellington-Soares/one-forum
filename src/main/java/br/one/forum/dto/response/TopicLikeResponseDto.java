package br.one.forum.dto.response;

public record TopicLikeResponseDto(
        Long topicId,
        Long likeCount
) {
}
