package br.one.forum.dtos;

public record CategoryResponseDto(
        Integer id,
        String name,
        Long topicCount
) {
}
