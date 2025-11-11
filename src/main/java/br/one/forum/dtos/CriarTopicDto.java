package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarTopicDto(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull Integer userId
) {}

