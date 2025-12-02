package br.one.forum.dtos.response;

public record CategoryResponseDto(
        Integer id,
        String name,
        Long topicCount
) {
}
