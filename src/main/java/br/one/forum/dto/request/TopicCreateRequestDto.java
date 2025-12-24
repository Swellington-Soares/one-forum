package br.one.forum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TopicCreateRequestDto(
        @Size(max = 100)
        @NotBlank
        String title,

        @NotBlank
        @Size(max = 1500)
        String content,

        @NotNull
        @NotEmpty
        List<@NotBlank String> categories
) {
}
