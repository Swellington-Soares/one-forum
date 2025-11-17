package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentDto(
        @NotBlank String content
) {}

