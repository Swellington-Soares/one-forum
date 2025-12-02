package br.one.forum.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(
        @NotBlank String content
) {
}
