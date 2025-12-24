package br.one.forum.dto.response;

public record CategoryResponseDto(
        Long id,
        String name,
        Long topicCount
) {
}
