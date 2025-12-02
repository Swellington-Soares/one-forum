package br.one.forum.dtos.response;

public record TopicLikeResponseDto(
        int topicId,
        int likeCount
) {
}
