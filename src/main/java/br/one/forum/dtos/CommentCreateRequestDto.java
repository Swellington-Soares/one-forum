package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(
        @NotBlank String content
) {
}
