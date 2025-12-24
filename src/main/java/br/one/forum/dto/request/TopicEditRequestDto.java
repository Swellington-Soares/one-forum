package br.one.forum.dto.request;

import jakarta.validation.constraints.Size;

public record TopicEditRequestDto(
        @Size(max = 60)
        String title,

        @Size(max = 1500)
        String content
) {
}
