package br.one.forum.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record UpdateTopicRequestDto(
        @Size(max = 100)
        String title,


        String content
) {
}
