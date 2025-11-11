package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarCommentDto(
        @NotNull Integer topicId,
        @NotNull Integer userId,
        @NotBlank String content
) {
}
