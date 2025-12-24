package br.one.forum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequestDto(
        @NotBlank
        @Size(max = 1500)
        String content
) {
}
