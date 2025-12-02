package br.one.forum.dtos.request;

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
        String content,

        @NotNull
        @NotEmpty
        List<@NotBlank String> categories

) {
}
