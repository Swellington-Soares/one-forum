package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentDto(
        @NotNull Integer topicId,
        @NotNull Integer userId,
        @NotBlank String content
) {
}
